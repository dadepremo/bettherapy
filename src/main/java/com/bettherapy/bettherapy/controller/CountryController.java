package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.model.entity.Country;
import com.bettherapy.bettherapy.model.repository.CountryRepository;
import com.bettherapy.bettherapy.model.request.CountryRequest;
import com.bettherapy.bettherapy.model.response.CountryResponse;
import com.bettherapy.bettherapy.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    @Autowired
    CountryService countryService;

    @PostMapping("/save")
    public ResponseEntity<CountryResponse> saveLeague(@RequestBody CountryRequest dto) {
        return ResponseEntity.ok(countryService.save(dto));
    }

    @GetMapping("/")
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        return ResponseEntity.ok(
                countryService.findAll().stream()
                        .map(countryService::toResponse)
                        .toList()
        );
    }

}