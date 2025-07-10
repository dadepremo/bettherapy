package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.model.entity.BetOption;
import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.util.BetResult;
import com.bettherapy.bettherapy.util.BetStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BetResponse {
    private Long id;
    private BigDecimal stake;
    private BigDecimal odds;
    private BigDecimal payout;
    private BetStatus status;
    private LocalDateTime placedAt;
    private Long userId;
    private Long betOptionId;
    private Long matchId;
    private BigDecimal potentialPayout;
    private Match match;
    private String optionDescription;
    private Double optionOdds;
    private BetResult optionResult;
}