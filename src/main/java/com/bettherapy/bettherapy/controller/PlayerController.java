package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.model.request.PlayerRequest;
import com.bettherapy.bettherapy.model.request.TeamRequest;
import com.bettherapy.bettherapy.model.response.PlayerResponse;
import com.bettherapy.bettherapy.model.response.TeamResponse;
import com.bettherapy.bettherapy.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    PlayerService playerService;

    @PostMapping("/save")
    public ResponseEntity<PlayerResponse> savePlayer(@RequestBody PlayerRequest request) {
        return ResponseEntity.ok(playerService.savePlayer(request));
    }

    @PostMapping("/saveAll")
    public ResponseEntity<List<PlayerResponse>> saveAllTeams(@RequestBody List<PlayerRequest> dtos) {
        return ResponseEntity.ok(playerService.savePlayers(dtos));
    }

    @GetMapping("/byTeam/{teamId}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    @GetMapping("/byNationality/{nationality}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByNationality(@PathVariable String nationality) {
        return ResponseEntity.ok(playerService.getPlayersByNationality(nationality));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

}
