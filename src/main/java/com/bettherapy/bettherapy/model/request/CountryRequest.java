package com.bettherapy.bettherapy.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CountryRequest {
    private String iso;
    private String name;
    private String url;
    private String phoneCode;
    private String continent;
    private String nationality;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
