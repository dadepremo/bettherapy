package com.bettherapy.bettherapy.model.response;

import com.bettherapy.bettherapy.model.entity.BetOption;
import com.bettherapy.bettherapy.model.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MatchOptions {
    private Match match;
    private List<BetOption> betOptions;
}

