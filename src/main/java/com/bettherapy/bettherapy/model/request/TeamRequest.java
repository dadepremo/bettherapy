package com.bettherapy.bettherapy.model.request;

import com.bettherapy.bettherapy.model.entity.Country;
import lombok.Data;

@Data
public class TeamRequest {
    private String name;
    private String shortName;
    private String logoUrl;
    private String foundedYear;
    private String stadiumName;
    private boolean isNationalTeam;
    private Long leagueId;
    private Long countryId;
}
