package com.bettherapy.bettherapy.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class BetRequest {
    private BigDecimal stake;
    private BigDecimal odds;
    private Long userId;
    private Long betOptionId;
    private Long matchId;
}
