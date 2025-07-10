package com.bettherapy.bettherapy.service;

import java.util.List;
import java.util.stream.Collectors;

import com.bettherapy.bettherapy.model.entity.Country;
import com.bettherapy.bettherapy.model.entity.League;
import com.bettherapy.bettherapy.model.entity.Sport;
import com.bettherapy.bettherapy.model.repository.CountryRepository;
import com.bettherapy.bettherapy.model.repository.LeagueRepository;
import com.bettherapy.bettherapy.model.repository.SportRepository;
import com.bettherapy.bettherapy.model.request.LeagueRequest;
import com.bettherapy.bettherapy.model.response.LeagueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LeagueService {

    @Autowired
    LeagueRepository leagueRepository;

    @Autowired
    SportRepository sportRepository;

    @Autowired
    CountryRepository countryRepository;

    public LeagueResponse saveLeague(LeagueRequest dto) {
        Sport sport = sportRepository.findById(dto.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found with ID: " + dto.getSportId()));

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found with ID: " + dto.getCountryId()));

        League league = new League();
        league.setName(dto.getName());
        league.setDescription(dto.getDescription());
        league.setSeason(dto.getSeason());
        league.setSport(sport);
        league.setCountry(country);
        league.setCup(dto.isCup());

        League saved = leagueRepository.save(league);

        return toResponse(saved);
    }

    public List<LeagueResponse> saveLeagues(List<LeagueRequest> dtos) {
        return dtos.stream()
                .map(this::saveLeague)
                .collect(Collectors.toList());
    }

    public List<LeagueResponse> getAllLeagues() {
        return leagueRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<LeagueResponse> getLeagueById(Long id) {
        return leagueRepository.findById(id).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private LeagueResponse toResponse(League league) {
        LeagueResponse dto = new LeagueResponse();
        dto.setId(league.getId());
        dto.setName(league.getName());
        dto.setDescription(league.getDescription());
        dto.setSeason(league.getSeason());
        dto.setSportId(league.getSport().getId());
        dto.setSportName(league.getSport().getName());
        dto.setCup(league.isCup());
        return dto;
    }
}
