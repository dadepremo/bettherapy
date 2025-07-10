package com.bettherapy.bettherapy.model.request;

import lombok.Data;

@Data
public class LeagueStandingRequest {
    private Long leagueId;
    private Long teamId;
    private String groupName;

    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
}
