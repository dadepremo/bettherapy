package com.bettherapy.bettherapy.service;


import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.model.entity.Team;
import com.bettherapy.bettherapy.model.repository.TeamRepository;
import com.bettherapy.bettherapy.model.response.StandingResponse;
import com.bettherapy.bettherapy.model.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bettherapy.bettherapy.util.MatchStatus;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StandingService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    public List<StandingResponse> getStandingsByLeague(Long leagueId) {
        List<Match> matches = matchRepository.findByLeague_IdAndStatus(leagueId, MatchStatus.COMPLETED);

        if (matches.isEmpty()) {
            return teamRepository.findByLeague_IdOrderByNameAsc(leagueId).stream()
                    .map(StandingResponse::new)
                    .collect(Collectors.toList());
        }

        Map<Long, StandingResponse> standings = new HashMap<>();

        for (Match match : matches) {
            Team home = match.getHomeTeam();
            Team away = match.getAwayTeam();

            standings.putIfAbsent(home.getId(), new StandingResponse(home));
            standings.putIfAbsent(away.getId(), new StandingResponse(away));

            StandingResponse homeStanding = standings.get(home.getId());
            StandingResponse awayStanding = standings.get(away.getId());

            int homeScore = match.getHomeScore();
            int awayScore = match.getAwayScore();

            if (homeScore > awayScore) {
                homeStanding.addWin();
                awayStanding.addLoss();
            } else if (homeScore < awayScore) {
                awayStanding.addWin();
                homeStanding.addLoss();
            } else {
                homeStanding.addDraw();
                awayStanding.addDraw();
            }

            homeStanding.addGoalsFor(homeScore);
            homeStanding.addGoalsAgainst(awayScore);

            awayStanding.addGoalsFor(awayScore);
            awayStanding.addGoalsAgainst(homeScore);

        }

        return sortStandings(standings);

    }

    private static List<StandingResponse> sortStandings(Map<Long, StandingResponse> standings) {
        List<StandingResponse> standingsList = new ArrayList<>(standings.values());

        for (int i = 0; i < standingsList.size() - 1; i++) {
            for (int j = i + 1; j < standingsList.size(); j++) {
                StandingResponse a = standingsList.get(i);
                StandingResponse b = standingsList.get(j);

                boolean shouldSwap = false;

                if (b.getPoints() > a.getPoints()) {
                    shouldSwap = true;
                } else if (b.getPoints() == a.getPoints()) {
                    if (b.getGoalDifference() > a.getGoalDifference()) {
                        shouldSwap = true;
                    } else if (b.getGoalDifference() == a.getGoalDifference()) {
                        if (b.getTeam().getName().compareTo(a.getTeam().getName()) < 0) {
                            shouldSwap = true;
                        }
                    }
                }

                if (shouldSwap) {
                    standingsList.set(i, b);
                    standingsList.set(j, a);
                }
            }
        }
        return standingsList;
    }

}
