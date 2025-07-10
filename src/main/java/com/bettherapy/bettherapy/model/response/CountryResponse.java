package com.bettherapy.bettherapy.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CountryResponse {
    private Long id;
    private String iso;
    private String name;
    private String url;
    private String phoneCode;
    private String continent;
    private String nationality;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
