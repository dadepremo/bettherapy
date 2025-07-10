package com.bettherapy.bettherapy.controller;


import com.bettherapy.bettherapy.model.request.BetOptionRequest;
import com.bettherapy.bettherapy.model.response.BetOptionResponse;
import com.bettherapy.bettherapy.service.BetOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bet-options")
@RequiredArgsConstructor
public class BetOptionController {

    private final BetOptionService betOptionService;

    @PostMapping
    public ResponseEntity<BetOptionResponse> create(@RequestBody BetOptionRequest request) {
        return ResponseEntity.ok(betOptionService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BetOptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(betOptionService.getById(id));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<BetOptionResponse>> getByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(betOptionService.getByMatch(matchId));
    }
}

