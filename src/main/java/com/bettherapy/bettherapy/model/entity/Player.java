package com.bettherapy.bettherapy.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String position; // e.g., "Goalkeeper", "Defender", "Midfielder", "Forward"

    private Integer jerseyNumber;

    private LocalDate dateOfBirth; // You can use LocalDate if preferred

    private Double height; // in cm

    private Double weight; // in kg

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "national_team_id")
    private Team nationalTeam;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
