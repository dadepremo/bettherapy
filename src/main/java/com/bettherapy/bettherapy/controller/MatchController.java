package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.model.repository.BetOptionRepository;
import com.bettherapy.bettherapy.model.repository.MatchRepository;
import com.bettherapy.bettherapy.model.request.BetOptionRequest;
import com.bettherapy.bettherapy.model.request.MatchRequest;
import com.bettherapy.bettherapy.model.response.MatchOptions;
import com.bettherapy.bettherapy.model.response.MatchResponse;
import com.bettherapy.bettherapy.service.MatchService;
import com.bettherapy.bettherapy.util.LoggableComponent;
import com.bettherapy.bettherapy.util.MatchStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    MatchService matchService;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    LoggableComponent logger;

    @Autowired
    BetOptionRepository betOptionRepository;

    @Operation(summary = "Save a match", description = "Saves the given match")
    @PostMapping("/save")
    public ResponseEntity<MatchResponse> saveMatch(@RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.saveMatch(request));
    }

    @Operation(summary = "Get matches by league ID", description = "Fetches league matches for a specific league")
    @GetMapping("/byLeague/{leagueId}")
    public ResponseEntity<List<MatchResponse>> getMatchesByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(matchService.getMatchesByLeague(leagueId));
    }

    @Operation(summary = "Generate fixtures for a certain league ID", description = "Generate season matches for a certain league")
    @PostMapping("/generate/{leagueId}")
    public ResponseEntity<List<MatchResponse>> generateMatches(@PathVariable Long leagueId) {
        List<Match> matches = matchService.generateLeagueMatches(leagueId);
        List<MatchResponse> responses = matches.stream().map(matchService::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get a matches by team ID", description = "Fetches matches for a specific team ID")
    @GetMapping("/byTeam/{teamId}")
    public ResponseEntity<List<MatchResponse>> getMatchesByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(matchService.getMatchesByTeam(teamId));
    }

    @Operation(summary = "Get a match by ID", description = "Fetches a specific match from the database")
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Long id) {
        return matchService.getMatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get last 5 matches for a specific team ID", description = "Get last 5 matches for a specific team ID")
    @GetMapping("/recent/{teamId}")
    public List<Match> getRecentMatches(@PathVariable Long teamId, @RequestParam(defaultValue = "5") int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return matchRepository.findRecentByTeamId(teamId, pageable);
    }

    @Operation(summary = "Generates world cup matches for a specific league ID", description = "Generates world cup matches for a specific league ID")
    @PostMapping("/generateWorldCup")
    public ResponseEntity<List<MatchResponse>> generateWorldCup(
            @RequestParam Long leagueId,
            @RequestParam int groupSize,
            @RequestParam int qualifyPerGroup) {

        if (groupSize <= 1 || qualifyPerGroup < 1) {
            return ResponseEntity.badRequest().build();
        }

        List<Match> matches = matchService.generateWorldCup(leagueId, groupSize, qualifyPerGroup);
        List<MatchResponse> responses = matches.stream().map(matchService::toResponse).toList();
        logger.info("Generating World Cup for leagueId={}, groupSize={}, qualifyPerGroup={}", leagueId, groupSize, qualifyPerGroup);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/byStage")
    public ResponseEntity<List<MatchResponse>> getMatchesByStage(
            @RequestParam Long leagueId,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String stageLabel,
            @RequestParam(required = false) String matchType // e.g., "GROUP_STAGE", "KNOCKOUT"
    ) {
        List<MatchResponse> matches = matchService.getMatchesByStageOrGroup(leagueId, groupName, stageLabel, matchType);
        return ResponseEntity.ok(matches);
    }

    @Operation(summary = "Get bracket matches by league ID", description = "Fetches bracket matches for a specific league")
    @GetMapping("/bracket/{leagueId}")
    public ResponseEntity<List<MatchResponse>> getBracketMatches(@PathVariable Long leagueId) {
        List<MatchResponse> knockoutMatches = matchService.getBracketMatches(leagueId);
        return ResponseEntity.ok(knockoutMatches);
    }


    @Operation(summary = "Update a match by ID", description = "Fetches a specific match from the database")
    @PutMapping("/{id}")
    public ResponseEntity<MatchResponse> updateMatch(
            @PathVariable Long id,
            @RequestBody MatchRequest request
    ) {
        MatchResponse response = matchService.updateMatch(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trending")
    public List<MatchOptions> getTrendingMatches() {
        Pageable top8 = PageRequest.of(0, 8);
        List<Match> matches = matchRepository.findTop8ByStatusOrderByKickoffAsc(MatchStatus.ONGOING, top8);

        return matches.stream()
                .map(match -> {
                    // Force fetch required nested fields
                    match.getHomeTeam().getName();  // triggers lazy load
                    match.getAwayTeam().getName();
                    return new MatchOptions(match, betOptionRepository.findByMatch(match));
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/bet-options")
    public ResponseEntity<?> createBetOptions(@RequestBody List<BetOptionRequest> requests) {
        matchService.saveBetOptions(requests);
        return ResponseEntity.ok().build();
    }


}
