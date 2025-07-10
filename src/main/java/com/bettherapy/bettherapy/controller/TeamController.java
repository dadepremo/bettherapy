package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.model.entity.Country;
import com.bettherapy.bettherapy.model.entity.Team;
import com.bettherapy.bettherapy.model.repository.TeamRepository;
import com.bettherapy.bettherapy.model.request.LeagueRequest;
import com.bettherapy.bettherapy.model.request.TeamRequest;
import com.bettherapy.bettherapy.model.response.LeagueResponse;
import com.bettherapy.bettherapy.model.response.TeamDetailsResponse;
import com.bettherapy.bettherapy.model.response.TeamResponse;
import com.bettherapy.bettherapy.service.TeamService;
import com.bettherapy.bettherapy.util.LoggableComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    LoggableComponent logger;

    @GetMapping("")
    public ResponseEntity<List<TeamResponse>> getTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @PostMapping("/save")
    public ResponseEntity<TeamResponse> saveTeam(@RequestBody TeamRequest request) {
        logger.info("Saving team: " + request);
        return ResponseEntity.ok(teamService.saveTeam(request));
    }

    @GetMapping("/byLeague/{leagueId}")
    public ResponseEntity<List<TeamResponse>> getTeamsByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(teamService.getTeamsByLeague(leagueId));
    }

    @PostMapping("/saveAll")
    public ResponseEntity<List<TeamResponse>> saveAllTeams(@RequestBody List<TeamRequest> dtos) {
        logger.info("Saving teams: " + dtos);
        return ResponseEntity.ok(teamService.saveTeams(dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDetailsResponse> getTeamById(@PathVariable Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));


        TeamDetailsResponse response = new TeamDetailsResponse(
                team.getId(),
                team.getName(),
                team.getShortName(),
                team.getFoundedYear(),
                team.isNationalTeam(),
                team.getLogoUrl(),
                team.getStadiumName(),
                team.getLeague().getName(),
                team.getLeague().getId(),
                team.getCountry()
        );

        return ResponseEntity.ok(response);
    }

}

