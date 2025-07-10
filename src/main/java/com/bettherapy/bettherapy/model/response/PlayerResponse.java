package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.model.entity.Country;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayerResponse {
    private Long id;
    private String fullName;
    private String position;
    private Integer jerseyNumber;
    private LocalDate dateOfBirth;
    private Double height;
    private Double weight;
    private String imageUrl;
    private Long teamId;
    private String teamName;
    private Country country;
}
