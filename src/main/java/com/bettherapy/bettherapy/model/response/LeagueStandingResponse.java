package com.bettherapy.bettherapy.model.response;

import lombok.Data;

@Data
public class LeagueStandingResponse {
    private Long id;
    private Long leagueId;
    private Long teamId;
    private String teamName;
    private String groupName;

    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    private int points;
}
