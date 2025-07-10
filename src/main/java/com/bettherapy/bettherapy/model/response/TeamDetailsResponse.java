package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.model.entity.Country;

public record TeamDetailsResponse(
        Long id,
        String name,
        String shortName,
        String foundedYear,
        Boolean isNationalTeam,
        String logoUrl,
        String stadiumName,
        String leagueName,
        Long leagueId,
        Country country
) {}
