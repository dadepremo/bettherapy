package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.util.BetResult;
import lombok.Data;

@Data
public class BetOptionResponse {
    private Long id;
    private BetResult result;
    private Double odds;
    private Long matchId;
    private String description;
}
