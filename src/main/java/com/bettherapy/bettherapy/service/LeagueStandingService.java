package com.bettherapy.bettherapy.service;


import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.model.request.LeagueStandingRequest;
import com.bettherapy.bettherapy.model.response.LeagueStandingResponse;
import com.bettherapy.bettherapy.model.entity.League;
import com.bettherapy.bettherapy.model.entity.LeagueStanding;
import com.bettherapy.bettherapy.model.entity.Team;
import com.bettherapy.bettherapy.model.repository.LeagueRepository;
import com.bettherapy.bettherapy.model.repository.LeagueStandingRepository;
import com.bettherapy.bettherapy.model.repository.TeamRepository;
import com.bettherapy.bettherapy.util.MatchStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueStandingService {

    @Autowired
    LeagueStandingRepository standingRepository;

    @Autowired
    LeagueRepository leagueRepository;

    @Autowired
    TeamRepository teamRepository;

    public List<LeagueStandingResponse> getStandings(Long leagueId) {
        return standingRepository.findByLeagueIdOrderByGroupNameAscPointsDescGoalDifferenceDescGoalsForDesc(leagueId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeagueStandingResponse createOrUpdateStanding(LeagueStandingRequest request) {
        League league = leagueRepository.findById(request.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found"));
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        LeagueStanding standing = standingRepository
                .findByLeagueIdAndTeamId(request.getLeagueId(), request.getTeamId())
                .orElse(new LeagueStanding());

        standing.setLeague(league);
        standing.setTeam(team);
        standing.setGroupName(request.getGroupName());
        standing.setPlayed(request.getPlayed());
        standing.setWins(request.getWins());
        standing.setDraws(request.getDraws());
        standing.setLosses(request.getLosses());
        standing.setGoalsFor(request.getGoalsFor());
        standing.setGoalsAgainst(request.getGoalsAgainst());
        standing.recalculate();

        return mapToResponse(standingRepository.save(standing));
    }

    @Transactional
    public void updateStandingsAfterMatch(Match match) {
        if (!match.getStatus().equals(MatchStatus.COMPLETED)) return;

        League league = match.getLeague();
        Long leagueId = league.getId();
        Team home = match.getHomeTeam();
        Team away = match.getAwayTeam();
        int homeGoals = match.getHomeScore();
        int awayGoals = match.getAwayScore();

        updateStanding(leagueId, home, homeGoals, awayGoals);
        updateStanding(leagueId, away, awayGoals, homeGoals);
    }

    private void updateStanding(Long leagueId, Team team, int goalsFor, int goalsAgainst) {

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("No league found for id: " + leagueId));

        LeagueStanding standing = standingRepository
                .findByLeagueIdAndTeamId(leagueId, team.getId())
                .orElseGet(() -> {
                    LeagueStanding s = new LeagueStanding();
                    s.setLeague(league);
                    s.setTeam(team);
                    return s;
                });

        standing.setPlayed(standing.getPlayed() + 1);
        standing.setGoalsFor(standing.getGoalsFor() + goalsFor);
        standing.setGoalsAgainst(standing.getGoalsAgainst() + goalsAgainst);

        if (goalsFor > goalsAgainst) {
            standing.setWins(standing.getWins() + 1);
        } else if (goalsFor == goalsAgainst) {
            standing.setDraws(standing.getDraws() + 1);
        } else {
            standing.setLosses(standing.getLosses() + 1);
        }

        standing.recalculate();
        standingRepository.save(standing);
    }

    private LeagueStandingResponse mapToResponse(LeagueStanding s) {
        LeagueStandingResponse r = new LeagueStandingResponse();
        r.setId(s.getId());
        r.setLeagueId(s.getLeague().getId());
        r.setTeamId(s.getTeam().getId());
        r.setTeamName(s.getTeam().getName());
        r.setGroupName(s.getGroupName());
        r.setPlayed(s.getPlayed());
        r.setWins(s.getWins());
        r.setDraws(s.getDraws());
        r.setLosses(s.getLosses());
        r.setGoalsFor(s.getGoalsFor());
        r.setGoalsAgainst(s.getGoalsAgainst());
        r.setGoalDifference(s.getGoalDifference());
        r.setPoints(s.getPoints());
        return r;
    }
}
