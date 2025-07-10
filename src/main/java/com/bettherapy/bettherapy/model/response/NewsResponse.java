package com.bettherapy.bettherapy.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsResponse {
    private Long id;
    private String message;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean visible;
}