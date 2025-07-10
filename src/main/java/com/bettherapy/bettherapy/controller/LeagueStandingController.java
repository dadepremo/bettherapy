package com.bettherapy.bettherapy.controller;


import com.bettherapy.bettherapy.model.request.LeagueStandingRequest;
import com.bettherapy.bettherapy.model.response.LeagueStandingResponse;
import com.bettherapy.bettherapy.service.LeagueStandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
@RequiredArgsConstructor
public class LeagueStandingController {

    private final LeagueStandingService service;

    @GetMapping("/{leagueId}")
    public List<LeagueStandingResponse> getStandings(@PathVariable Long leagueId) {
        return service.getStandings(leagueId);
    }

    @PostMapping
    public LeagueStandingResponse saveStanding(@RequestBody LeagueStandingRequest request) {
        return service.createOrUpdateStanding(request);
    }
}
