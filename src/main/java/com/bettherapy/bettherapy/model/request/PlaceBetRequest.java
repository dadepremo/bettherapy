package com.bettherapy.bettherapy.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceBetRequest {
    private Long userId;
    private Long betOptionId;
    private BigDecimal amount; // The stake
}