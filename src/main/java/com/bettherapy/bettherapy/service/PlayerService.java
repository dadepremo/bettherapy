package com.bettherapy.bettherapy.service;

import com.bettherapy.bettherapy.model.entity.Country;
import com.bettherapy.bettherapy.model.entity.Player;
import com.bettherapy.bettherapy.model.entity.Team;
import com.bettherapy.bettherapy.model.repository.CountryRepository;
import com.bettherapy.bettherapy.model.request.LeagueRequest;
import com.bettherapy.bettherapy.model.request.PlayerRequest;
import com.bettherapy.bettherapy.model.request.TeamRequest;
import com.bettherapy.bettherapy.model.response.LeagueResponse;
import com.bettherapy.bettherapy.model.response.PlayerResponse;
import com.bettherapy.bettherapy.model.repository.PlayerRepository;
import com.bettherapy.bettherapy.model.repository.TeamRepository;
import com.bettherapy.bettherapy.model.response.TeamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    CountryRepository countryRepository;

    public PlayerResponse savePlayer(PlayerRequest request) {
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + request.getTeamId()));

        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found with ID: " + request.getCountryId()));

        Player player = new Player();
        player.setFullName(request.getFullName());
        player.setPosition(request.getPosition());
        player.setCountry(country);
        player.setJerseyNumber(request.getJerseyNumber());
        player.setDateOfBirth(request.getDateOfBirth());
        player.setHeight(request.getHeight());
        player.setWeight(request.getWeight());
        player.setImageUrl(request.getImageUrl());
        player.setTeam(team);

        Player saved = playerRepository.save(player);
        return toResponse(saved);
    }

    public PlayerResponse getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        return toResponse(player);
    }

    public List<PlayerResponse> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PlayerResponse toResponse(Player player) {
        PlayerResponse response = new PlayerResponse();
        response.setId(player.getId());
        response.setFullName(player.getFullName());
        response.setPosition(player.getPosition());
        response.setCountry(player.getCountry());
        response.setJerseyNumber(player.getJerseyNumber());
        response.setDateOfBirth(player.getDateOfBirth());
        response.setHeight(player.getHeight());
        response.setWeight(player.getWeight());
        response.setImageUrl(player.getImageUrl());
        response.setTeamId(player.getTeam().getId());
        response.setTeamName(player.getTeam().getName());
        return response;
    }

    public List<PlayerResponse> savePlayers(List<PlayerRequest> dtos) {
        return dtos.stream()
                .map(this::savePlayer)
                .collect(Collectors.toList());
    }

    public List<PlayerResponse> getPlayersByNationality(String name) {
        return playerRepository.findByCountry_Name(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
