package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.model.response.StandingResponse;
import com.bettherapy.bettherapy.service.StandingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
public class StandingController {

    @Autowired
    private StandingService standingService;

    @GetMapping("/byLeague/{leagueId}")
    public ResponseEntity<List<StandingResponse>> getStandingsByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(standingService.getStandingsByLeague(leagueId));
    }
}
