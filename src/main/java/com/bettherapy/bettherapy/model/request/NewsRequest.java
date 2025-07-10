package com.bettherapy.bettherapy.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsRequest {
    private Long id;
    private String message;
    private String type;
    private boolean visible;
    private LocalDateTime expiresAt;
}