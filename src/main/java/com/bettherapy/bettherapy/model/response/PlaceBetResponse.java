package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.util.BetStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlaceBetResponse {
    private Long id;
    private Long userId;
    private Long betOptionId;
    private BigDecimal stake;
    private BigDecimal potentialPayout;
    private BetStatus status;
}