package com.bettherapy.bettherapy.model.request;

import com.bettherapy.bettherapy.util.MatchStatus;
import com.bettherapy.bettherapy.util.MatchType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchRequest {
    private LocalDateTime kickoff;
    private String venue;
    private String name;
    private MatchStatus status;
    private Integer homeScore;
    private Integer awayScore;
    private Long leagueId;
    private Long homeTeamId;
    private Long awayTeamId;
    private Integer matchday;

    // New, more flexible fields
    private MatchType matchType;      // LEAGUE, GROUP_STAGE, KNOCKOUT
    private String stageLabel;        // e.g., "Group A - Matchday 3", "Quarterfinal"
    private String groupName;         // e.g., "Group A", "Group B", etc.
    private Integer roundOrder;       // For sorting knockout stages
}
