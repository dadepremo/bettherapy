package com.bettherapy.bettherapy.model.response;


import com.bettherapy.bettherapy.model.entity.Country;
import lombok.Data;

@Data
public class LeagueResponse {
    private Long id;
    private String name;
    private String description;
    private String season;
    private Long sportId;
    private String sportName;
    private Country country;
    private boolean isCup;
}
