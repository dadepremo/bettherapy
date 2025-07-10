package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.service.PlayerImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
public class PlayerImportController {

    @Autowired
    private PlayerImportService importService;

    @PostMapping("/players/{localTeamId}/{externalTeamId}")
    public ResponseEntity<String> importPlayers(@PathVariable Long localTeamId,
                                                @PathVariable int externalTeamId) {
        try {
            importService.importPlayersForTeam(localTeamId, externalTeamId);
            return ResponseEntity.ok("✅ Players imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Failed to import players: " + e.getMessage());
        }
    }
}
