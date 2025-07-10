package com.bettherapy.bettherapy.service;

import com.bettherapy.bettherapy.model.entity.Country;
import com.bettherapy.bettherapy.model.entity.Player;
import com.bettherapy.bettherapy.model.entity.Team;
import com.bettherapy.bettherapy.model.repository.CountryRepository;
import com.bettherapy.bettherapy.model.repository.PlayerRepository;
import com.bettherapy.bettherapy.model.repository.TeamRepository;
import com.bettherapy.bettherapy.util.LoggableComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlayerImportService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private LoggableComponent logger;

    private static final String API_URL = "https://v3.football.api-sports.io/players?team={teamApiId}&season=2023";
    private static final String API_KEY = "";

    public void importPlayersForTeam(Long localTeamId, int externalTeamId) {
        Team team = teamRepository.findById(localTeamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        logger.info("Starting player import for: " + team.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                entity,
                Map.class,
                externalTeamId
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            logger.error("Failed to fetch players from API.");
            return;
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("response");

        if (data == null || data.isEmpty()) {
            logger.warn("No player data returned from API.");
            return;
        }

        logger.info("Players found: " + data.size());

        for (Map<String, Object> entry : data) {
            try {

                Map<String, Object> playerMap = (Map<String, Object>) entry.get("player");
                Map<String, Object> statistics = ((List<Map<String, Object>>) entry.get("statistics")).get(0);
                Map<String, Object> games = (Map<String, Object>) statistics.get("games");

                logger.info("Player: " + playerMap);

                String fullName = (String) playerMap.get("name");
                String nationality = (String) playerMap.get("nationality");
                String birthDate = (String) ((Map<String, Object>) playerMap.get("birth")).get("date");
                String imageUrl = (String) playerMap.get("photo");
                String position = (String) games.get("position");
                Integer jerseyNumber = games.get("number") != null
                        ? ((Number) games.get("number")).intValue()
                        : null;

                Country country = countryRepository.findByNameIgnoreCase(nationality)
                        .orElse(null); // You can implement country creation here if desired

                Player player = new Player();
                player.setFullName(fullName);
                player.setPosition(position);
                player.setDateOfBirth(LocalDate.parse(birthDate));
                player.setJerseyNumber(jerseyNumber);

                String heightStr = (String) playerMap.get("height");
                String weightStr = (String) playerMap.get("weight");

                Double height = heightStr != null && heightStr.contains("cm")
                        ? Double.parseDouble(heightStr.replace(" cm", ""))
                        : null;
                Double weight = weightStr != null && weightStr.contains("kg")
                        ? Double.parseDouble(weightStr.replace(" kg", ""))
                        : null;


                player.setHeight(height);
                player.setWeight(weight);
                player.setImageUrl(imageUrl);
                player.setTeam(team);
                player.setCountry(country);

                playerRepository.save(player);
                logger.info("Imported: " + fullName);
            } catch (Exception ex) {
                logger.error("Failed to import player entry: " + entry, ex);
            }
        }
    }
}
