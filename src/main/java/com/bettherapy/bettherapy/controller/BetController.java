package com.bettherapy.bettherapy.controller;


import com.bettherapy.bettherapy.model.request.BetRequest;
import com.bettherapy.bettherapy.model.request.PlaceBetRequest;
import com.bettherapy.bettherapy.model.response.BetResponse;
import com.bettherapy.bettherapy.model.response.PlaceBetResponse;
import com.bettherapy.bettherapy.service.BetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    @Autowired
    BetService betService;


    @GetMapping
    public ResponseEntity<List<BetResponse>> getAll() {
        return ResponseEntity.ok(betService.getAllBets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(betService.getBetById(id));
    }

    @PostMapping("/place")
    public BetResponse placeBet(@RequestBody BetRequest request) {
        return betService.placeBet(request);
    }

    @GetMapping("/user/{userId}")
    public List<BetResponse> getUserBets(@PathVariable Long userId) {
        return betService.getBetsByUser(userId);
    }

    @PostMapping("/settle/{matchId}")
    public ResponseEntity<String> settleBets(@PathVariable Long matchId) {
        betService.settleBetsForMatch(matchId);
        return ResponseEntity.ok("✅ Bets settled for match ID: " + matchId);
    }
}
