package com.bettherapy.bettherapy.model.request;

import com.bettherapy.bettherapy.util.BetResult;
import lombok.Data;

@Data
public class BetOptionRequest {
    private BetResult result;
    private Double odds;
    private Long matchId;
    private String description;
}
