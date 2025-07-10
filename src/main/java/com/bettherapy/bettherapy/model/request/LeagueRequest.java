package com.bettherapy.bettherapy.model.request;


import com.bettherapy.bettherapy.model.entity.Country;
import lombok.Data;

@Data
public class LeagueRequest {
    private String name;
    private String description;
    private String season;
    private Long sportId;
    private Long countryId;
    private boolean isCup;
}
