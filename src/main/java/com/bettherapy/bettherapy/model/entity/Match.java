package com.bettherapy.bettherapy.model.entity;

import com.bettherapy.bettherapy.util.MatchStatus;
import com.bettherapy.bettherapy.util.MatchType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime kickoff;

    private String venue;

    private String name;

    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.SCHEDULED;

    private Integer homeScore;

    private Integer awayScore;

    private Integer matchday;

    private String groupName;              // e.g. "Group A"
    private String knockoutRound = "none"; // Optional label: "Quarterfinal", etc.

    @Enumerated(EnumType.STRING)
    private MatchType type;                // LEAGUE, GROUP_STAGE, KNOCKOUT

    private Integer roundOrder;            // for knockout sorting

    private String stageLabel;             // e.g., "Final", "Group B - MD3"

    private Boolean isTwoLegged = false;
    private Integer legNumber;
    private Long aggregateMatchId;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Team winner;
}
