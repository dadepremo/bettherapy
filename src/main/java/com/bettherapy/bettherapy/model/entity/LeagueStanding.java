package com.bettherapy.bettherapy.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "league_standings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueStanding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private String groupName; // For group-based competitions

    private int played = 0;
    private int wins = 0;
    private int draws = 0;
    private int losses = 0;
    private int goalsFor = 0;
    private int goalsAgainst = 0;
    private int goalDifference = 0;
    private int points = 0;

    public void recalculate() {
        this.goalDifference = goalsFor - goalsAgainst;
        this.points = wins * 3 + draws;
    }
}
