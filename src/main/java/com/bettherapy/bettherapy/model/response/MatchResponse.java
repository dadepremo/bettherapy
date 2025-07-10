package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.util.MatchStatus;
import com.bettherapy.bettherapy.util.MatchType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchResponse {
    private Long id;
    private String name;
    private LocalDateTime kickoff;
    private String venue;
    private MatchStatus status;
    private Integer homeScore;
    private Integer awayScore;

    private Long leagueId;
    private String leagueName;

    private Long homeTeamId;
    private String homeTeamName;
    private String homeTeamLogo;

    private Long awayTeamId;
    private String awayTeamName;
    private String awayTeamLogo;

    private Integer matchday;

    // New fields for more versatile competition structure
    private MatchType matchType;      // GROUP_STAGE, KNOCKOUT, LEAGUE, etc.
    private String stageLabel;        // "Group A - MD3", "Quarterfinal", etc.
    private String groupName;         // "Group A", "Group B", etc.
    private Integer roundOrder;       // Used for sorting knockout rounds
}
