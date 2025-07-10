package com.bettherapy.bettherapy.controller;

import com.bettherapy.bettherapy.model.entity.Sport;
import com.bettherapy.bettherapy.model.entity.User;
import com.bettherapy.bettherapy.model.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
public class SportController {

    @Autowired
    SportRepository sportRepository;

    @GetMapping("/")
    public List<Sport> getSports() {
        return sportRepository.findAll();
    }

    @PostMapping("/saveSport")
    public Sport saveSport(@RequestBody Sport sport) {
        return sportRepository.save(sport);
    }
}
