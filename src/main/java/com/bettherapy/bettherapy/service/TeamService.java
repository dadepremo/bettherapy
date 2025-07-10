package com.bettherapy.bettherapy.service;


import com.bettherapy.bettherapy.model.entity.Country;
import com.bettherapy.bettherapy.model.entity.League;
import com.bettherapy.bettherapy.model.entity.Team;
import com.bettherapy.bettherapy.model.repository.CountryRepository;
import com.bettherapy.bettherapy.model.request.LeagueRequest;
import com.bettherapy.bettherapy.model.request.TeamRequest;
import com.bettherapy.bettherapy.model.response.LeagueResponse;
import com.bettherapy.bettherapy.model.response.TeamResponse;
import com.bettherapy.bettherapy.model.repository.LeagueRepository;
import com.bettherapy.bettherapy.model.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    LeagueRepository leagueRepository;

    @Autowired
    CountryRepository countryRepository;

    public TeamResponse saveTeam(TeamRequest request) {
        League league = leagueRepository.findById(request.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found: " + request.getLeagueId()));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found: " + request.getCountryId()));

        Team team = new Team();
        team.setName(request.getName());
        team.setShortName(request.getShortName());
        team.setCountry(country);
        team.setLogoUrl(request.getLogoUrl());
        team.setNationalTeam(request.isNationalTeam());
        team.setFoundedYear(request.getFoundedYear());
        team.setStadiumName(request.getStadiumName());
        team.setLeague(league);

        Team saved = teamRepository.save(team);
        return toResponse(saved);
    }

    public List<TeamResponse> saveTeams(List<TeamRequest> dtos) {
        return dtos.stream()
                .map(this::saveTeam)
                .collect(Collectors.toList());
    }

    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TeamResponse> getTeamsByLeague(Long leagueId) {
        return teamRepository.findByLeagueId(leagueId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TeamResponse toResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setShortName(team.getShortName());
        response.setCountry(team.getCountry());
        response.setLogoUrl(team.getLogoUrl());
        response.setLeagueId(team.getLeague().getId());
        response.setLeagueName(team.getLeague().getName());
        return response;
    }
}

