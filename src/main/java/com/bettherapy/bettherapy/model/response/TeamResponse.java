package com.bettherapy.bettherapy.model.response;


import com.bettherapy.bettherapy.model.entity.Country;
import lombok.Data;

@Data
public class TeamResponse {
    private Long id;
    private String name;
    private String shortName;
    private Country country;
    private String logoUrl;
    private Long leagueId;
    private String foundedYear;
    private String stadiumName;
    private boolean isNationalTeam;
    private String leagueName;
}
