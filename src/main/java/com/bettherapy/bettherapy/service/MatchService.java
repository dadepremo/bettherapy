package com.bettherapy.bettherapy.service;

import com.bettherapy.bettherapy.model.entity.BetOption;
import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.model.entity.League;
import com.bettherapy.bettherapy.model.entity.Team;
import com.bettherapy.bettherapy.model.repository.BetOptionRepository;
import com.bettherapy.bettherapy.model.request.BetOptionRequest;
import com.bettherapy.bettherapy.model.request.MatchRequest;
import com.bettherapy.bettherapy.model.response.MatchResponse;
import com.bettherapy.bettherapy.model.repository.MatchRepository;
import com.bettherapy.bettherapy.model.repository.LeagueRepository;
import com.bettherapy.bettherapy.model.repository.TeamRepository;
import com.bettherapy.bettherapy.util.LoggableComponent;
import com.bettherapy.bettherapy.util.MatchStatus;
import com.bettherapy.bettherapy.util.MatchType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    LeagueRepository leagueRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    LoggableComponent logger;

    @Autowired
    LeagueStandingService leagueStandingService;

    @Autowired
    BetService betService;

    @Autowired
    BetOptionRepository betOptionRepository;

    public MatchResponse saveMatch(MatchRequest request) {
        League league = leagueRepository.findById(request.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found: " + request.getLeagueId()));

        Team home = teamRepository.findById(request.getHomeTeamId())
                .orElseThrow(() -> new RuntimeException("Home team not found: " + request.getHomeTeamId()));

        Team away = teamRepository.findById(request.getAwayTeamId())
                .orElseThrow(() -> new RuntimeException("Away team not found: " + request.getAwayTeamId()));

        Match match = new Match();
        match.setKickoff(request.getKickoff());
        match.setVenue(request.getVenue());
        match.setName(request.getName());
        match.setStatus(request.getStatus() != null ? request.getStatus() : MatchStatus.SCHEDULED);
        match.setHomeScore(request.getHomeScore());
        match.setAwayScore(request.getAwayScore());
        match.setLeague(league);
        match.setHomeTeam(home);
        match.setAwayTeam(away);

        match.setType(request.getMatchType());              // optional
        match.setGroupName(request.getGroupName());         // optional
        match.setStageLabel(request.getStageLabel());       // optional
        match.setRoundOrder(request.getRoundOrder());       // optional

        Match saved = matchRepository.save(match);

        if (match.getStatus() == MatchStatus.COMPLETED &&
                (match.getType() == MatchType.LEAGUE || match.getType() == MatchType.GROUP_STAGE)) {
            leagueStandingService.updateStandingsAfterMatch(saved);
        }

        return toResponse(saved);
    }

    public List<MatchResponse> getMatchesByLeague(Long leagueId) {
        List<Match> matches = matchRepository.findByLeagueId(leagueId);
        return matches.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MatchResponse> getMatchesByTeam(Long teamId) {
        List<Match> matches = matchRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId);
        return matches.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<MatchResponse> getMatchById(Long id) {
        return matchRepository.findById(id)
                .map(this::toResponse);
    }

    @Transactional
    public List<Match> generateLeagueMatches(Long leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        matchRepository.deleteAllByLeague(league);
        List<Team> teams = new ArrayList<>(teamRepository.findByLeagueId(leagueId));

        if (teams.size() < 2) {
            throw new IllegalStateException("At least 2 teams are required.");
        }

        // Add BYE if team count is odd
        if (teams.size() % 2 != 0) {
            Team bye = new Team();
            bye.setName("BYE");
            teams.add(bye);
        }

        List<Match> matches = new ArrayList<>();
        LocalDate baseDate = LocalDate.of(2025, 8, 8); // Friday of first match week
        LocalTime baseTime = LocalTime.of(15, 0);

        int totalRounds = teams.size() - 1;
        int matchesPerRound = teams.size() / 2;

        Collections.shuffle(teams);
        Team fixed = teams.get(0);
        List<Team> rotating = new ArrayList<>(teams.subList(1, teams.size()));

        DayOfWeek[] matchDays = {DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

        for (int round = 0; round < totalRounds; round++) {
            int matchday = round + 1;
            List<Match> unscheduled = new ArrayList<>();

            // Create pairings
            Team home = (round % 2 == 0) ? fixed : rotating.get(0);
            Team away = (round % 2 == 0) ? rotating.get(0) : fixed;
            if (!home.getName().equals("BYE") && !away.getName().equals("BYE")) {
                unscheduled.add(createLeagueMatch(home, away, league, matchday, null));
            }

            for (int i = 1; i < matchesPerRound; i++) {
                Team t1 = rotating.get(i);
                Team t2 = rotating.get(rotating.size() - i);
                if (t1.getName().equals("BYE") || t2.getName().equals("BYE")) continue;

                if (round % 2 == 0) {
                    unscheduled.add(createLeagueMatch(t1, t2, league, matchday, null));
                } else {
                    unscheduled.add(createLeagueMatch(t2, t1, league, matchday, null));
                }
            }

            // Schedule matches across Fri-Sun
            for (int i = 0; i < unscheduled.size(); i++) {
                int dayIndex = i % 3;
                int timeSlot = i / 3;

                LocalDate matchDate = baseDate.plusWeeks(matchday - 1);
                DayOfWeek desiredDay = matchDays[dayIndex];
                int dayOffset = desiredDay.getValue() - matchDate.getDayOfWeek().getValue();
                if (dayOffset < 0) dayOffset += 7;
                matchDate = matchDate.plusDays(dayOffset);

                LocalTime kickoffTime = baseTime.plusHours(timeSlot * 2L); // 15:00, 17:00, 19:00, ...

                unscheduled.get(i).setKickoff(LocalDateTime.of(matchDate, kickoffTime));
                matches.add(unscheduled.get(i));
            }

            // Rotate teams for next round
            rotating.add(0, rotating.remove(rotating.size() - 1));
        }

        // Add reverse fixtures
        int half = matches.size();
        for (int i = 0; i < half; i++) {
            Match m = matches.get(i);
            Match reverse = createLeagueMatch(
                    m.getAwayTeam(), m.getHomeTeam(), league,
                    m.getMatchday() + totalRounds,
                    m.getKickoff().plusWeeks(totalRounds)
            );
            matches.add(reverse);
        }

        logger.info("Generated {} matches for league {}", matches.size(), league.getName());
        return matchRepository.saveAll(matches);
    }

    @Transactional
    public MatchResponse updateMatch(Long id, MatchRequest request) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id " + id));

        // Update basic info if present
        if (request.getKickoff() != null) match.setKickoff(request.getKickoff());
        if (request.getVenue() != null) match.setVenue(request.getVenue());
        if (request.getName() != null) match.setName(request.getName());
        if (request.getStatus() != null) match.setStatus(request.getStatus());
        if (request.getHomeScore() != null) match.setHomeScore(request.getHomeScore());
        if (request.getAwayScore() != null) match.setAwayScore(request.getAwayScore());
        if (request.getMatchday() != null) match.setMatchday(request.getMatchday());
        if (request.getMatchType() != null) match.setType(request.getMatchType());
        if (request.getStageLabel() != null) match.setStageLabel(request.getStageLabel());
        if (request.getGroupName() != null) match.setGroupName(request.getGroupName());
        if (request.getRoundOrder() != null) match.setRoundOrder(request.getRoundOrder());

        // Update teams if needed
        if (request.getHomeTeamId() != null && !match.getHomeTeam().getId().equals(request.getHomeTeamId())) {
            Team newHome = teamRepository.findById(request.getHomeTeamId())
                    .orElseThrow(() -> new RuntimeException("Home team not found"));
            match.setHomeTeam(newHome);
        }
        if (request.getAwayTeamId() != null && !match.getAwayTeam().getId().equals(request.getAwayTeamId())) {
            Team newAway = teamRepository.findById(request.getAwayTeamId())
                    .orElseThrow(() -> new RuntimeException("Away team not found"));
            match.setAwayTeam(newAway);
        }

        Match updated = matchRepository.save(match);

        // Update standings only if COMPLETED and match is LEAGUE or GROUP_STAGE
        // Update standings and settle bets if match is completed
        if (updated.getStatus() == MatchStatus.COMPLETED &&
                (updated.getType() == MatchType.LEAGUE || updated.getType() == MatchType.GROUP_STAGE)) {

            leagueStandingService.updateStandingsAfterMatch(updated);
            betService.settleBetsForMatch(updated.getId());
        }

        return toResponse(updated);
    }

    @Transactional
    public List<Match> generateWorldCup(Long leagueId, int groupSize, int qualifyPerGroup) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        matchRepository.deleteAllByLeague(league);

        List<Team> teams = new ArrayList<>(teamRepository.findByLeagueId(leagueId));

        if (teams.size() % groupSize != 0) {
            throw new IllegalArgumentException("Total teams must be divisible by group size.");
        }

        Collections.shuffle(teams);

        int numGroups = teams.size() / groupSize;
        List<Match> allMatches = new ArrayList<>();
        LocalDateTime baseDate = LocalDateTime.of(2025, 11, 1, 15, 0);
        int matchdayOffset = 0;

        // --- GROUP STAGE ---
        for (int g = 0; g < numGroups; g++) {
            List<Team> group = teams.subList(g * groupSize, (g + 1) * groupSize);
            String groupName = "Group " + (char) ('A' + g);
            int matchday = g + 1;

            for (int i = 0; i < groupSize; i++) {
                for (int j = i + 1; j < groupSize; j++) {
                    Team home = group.get(i);
                    Team away = group.get(j);

                    Match match = createMatch(home, away, league, matchday, baseDate.plusDays(matchdayOffset));
                    match.setType(MatchType.GROUP_STAGE);
                    match.setGroupName(groupName);
                    match.setStageLabel(groupName + " - MD" + matchday);
                    allMatches.add(match);
                    matchdayOffset++;
                }
            }
        }

        matchRepository.saveAll(allMatches);

        // ⚠️ Placeholder: simulate group rankings
        List<Team> qualifiers = teams.subList(0, numGroups * qualifyPerGroup); // you can replace with actual standings

        // --- ELIMINATION STAGE ---
        int rounds = (int) (Math.log(qualifiers.size()) / Math.log(2));
        String[] roundLabels = { "Final", "Semifinal", "Quarterfinal", "Round of 16", "Round of 32", "Round of 64" };
        int knockoutDayOffset = 50;

        for (int r = rounds; r > 0; r--) {
            String roundName = roundLabels[r - 1];
            int matchday = 100 + rounds - r;
            int roundOrder = rounds - r + 1;
            List<Team> nextRound = new ArrayList<>();

            for (int i = 0; i < qualifiers.size(); i += 2) {
                Team home = qualifiers.get(i);
                Team away = qualifiers.get(i + 1);

                Match match = createMatch(home, away, league, matchday, baseDate.plusDays(knockoutDayOffset));
                match.setName(roundName + ": " + home.getName() + " vs " + away.getName());
                match.setType(MatchType.KNOCKOUT);
                match.setKnockoutRound(roundName);
                match.setStageLabel(roundName);
                match.setRoundOrder(roundOrder);
                match.setKickoff(baseDate.plusDays(knockoutDayOffset));
                allMatches.add(match);

                // placeholder winner
                nextRound.add(Math.random() > 0.5 ? home : away);
                knockoutDayOffset += 2;
            }

            qualifiers = nextRound;
        }

        logger.info("World Cup generated with {} total matches.", allMatches.size());
        return matchRepository.saveAll(allMatches);
    }

    public List<MatchResponse> getMatchesByStageOrGroup(Long leagueId, String groupName, String stageLabel, String type) {
        List<Match> matches;

        if (groupName != null) {
            matches = matchRepository.findByLeagueIdAndGroupName(leagueId, groupName);
        } else if (stageLabel != null) {
            matches = matchRepository.findByLeagueIdAndStageLabel(leagueId, stageLabel);
        } else if (type != null) {
            matches = matchRepository.findByLeagueIdAndType(leagueId, MatchType.valueOf(type.toUpperCase()));
        } else {
            matches = matchRepository.findByLeagueId(leagueId);
        }

        return matches.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<MatchResponse> getBracketMatches(Long leagueId) {
        return matchRepository.findByLeagueIdAndTypeOrderByRoundOrderAsc(leagueId, MatchType.KNOCKOUT)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void saveBetOptions(List<BetOptionRequest> requests) {
        List<BetOption> options = new ArrayList<>();

        for (BetOptionRequest req : requests) {
            Match match = matchRepository.findById(req.getMatchId())
                    .orElseThrow(() -> new RuntimeException("Match not found: " + req.getMatchId()));

            BetOption option = new BetOption();
            option.setMatch(match);
            option.setResult(req.getResult());
            option.setOdds(req.getOdds());
            option.setDescription(req.getDescription());

            options.add(option);
        }

        betOptionRepository.saveAll(options);
    }

    private Match createLeagueMatch(Team home, Team away, League league, int matchday, LocalDateTime kickoff) {
        Match match = new Match();
        match.setLeague(league);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setName(home.getName() + " vs " + away.getName());
        match.setKickoff(kickoff);
        match.setVenue(home.getStadiumName() != null ? home.getStadiumName() : home.getName() + " Stadium");
        match.setMatchday(matchday);
        match.setStatus(MatchStatus.SCHEDULED);
        match.setType(MatchType.LEAGUE);
        match.setStageLabel("Matchday " + matchday);
        return match;
    }

    private Match createMatch(Team home, Team away, League league, int matchday, LocalDateTime kickoff) {
        Match match = new Match();
        match.setLeague(league);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setName(home.getName() + " vs " + away.getName());
        match.setVenue(home.getStadiumName() != null ? home.getStadiumName() : home.getName() + " Stadium");
        match.setStatus(MatchStatus.SCHEDULED);
        match.setHomeScore(null);
        match.setAwayScore(null);
        match.setMatchday(matchday);
        match.setKickoff(kickoff);
        return match;
    }

    public MatchResponse toResponse(Match match) {
        MatchResponse res = new MatchResponse();
        res.setId(match.getId());
        res.setName(match.getName());
        res.setKickoff(match.getKickoff());
        res.setVenue(match.getVenue());
        res.setStatus(match.getStatus());
        res.setHomeScore(match.getHomeScore());
        res.setAwayScore(match.getAwayScore());
        res.setLeagueId(match.getLeague().getId());
        res.setLeagueName(match.getLeague().getName());
        res.setHomeTeamId(match.getHomeTeam().getId());
        res.setHomeTeamName(match.getHomeTeam().getName());
        res.setAwayTeamId(match.getAwayTeam().getId());
        res.setAwayTeamName(match.getAwayTeam().getName());
        res.setMatchday(match.getMatchday());
        res.setHomeTeamLogo(match.getHomeTeam().getLogoUrl());
        res.setAwayTeamLogo(match.getAwayTeam().getLogoUrl());
        res.setGroupName(match.getGroupName());
        res.setStageLabel(match.getStageLabel());
        res.setMatchType(match.getType());
        res.setRoundOrder(match.getRoundOrder());
        return res;
    }
}
