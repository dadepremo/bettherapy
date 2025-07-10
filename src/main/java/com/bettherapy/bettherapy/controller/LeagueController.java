package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.model.request.LeagueRequest;
import com.bettherapy.bettherapy.model.response.LeagueResponse;
import com.bettherapy.bettherapy.service.LeagueService;
import com.bettherapy.bettherapy.util.LoggableComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
public class LeagueController {

    @Autowired
    LeagueService leagueService;

    @Autowired
    LoggableComponent logger;

    @PostMapping("/save")
    public ResponseEntity<LeagueResponse> saveLeague(@RequestBody LeagueRequest dto) {
        logger.info("Saving league: " + dto);
        return ResponseEntity.ok(leagueService.saveLeague(dto));
    }

    @PostMapping("/saveAll")
    public ResponseEntity<List<LeagueResponse>> saveAllLeagues(@RequestBody List<LeagueRequest> dtos) {
        logger.info("Saving leagues: " + dtos);
        return ResponseEntity.ok(leagueService.saveLeagues(dtos));
    }

    @GetMapping
    public ResponseEntity<List<LeagueResponse>> getAllLeagues() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<LeagueResponse>> getLeagueById(@PathVariable Long id) {
        return ResponseEntity.ok(leagueService.getLeagueById(id));
    }
}
