package com.bettherapy.bettherapy.service;


import com.bettherapy.bettherapy.model.request.BetOptionRequest;
import com.bettherapy.bettherapy.model.response.BetOptionResponse;
import com.bettherapy.bettherapy.model.entity.BetOption;
import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.model.repository.BetOptionRepository;
import com.bettherapy.bettherapy.model.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BetOptionService {

    private final BetOptionRepository betOptionRepository;
    private final MatchRepository matchRepository;

    public BetOptionResponse create(BetOptionRequest request) {
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with ID: " + request.getMatchId()));

        BetOption option = BetOption.builder()
                .result(request.getResult())
                .odds(request.getOdds())
                .description(request.getDescription())
                .match(match)
                .build();

        return toDto(betOptionRepository.save(option));
    }

    public List<BetOptionResponse> getByMatch(Long matchId) {
        return betOptionRepository.findByMatchId(matchId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BetOptionResponse getById(Long id) {
        return betOptionRepository.findById(id).map(this::toDto).orElseThrow();
    }

    private BetOptionResponse toDto(BetOption option) {
        BetOptionResponse dto = new BetOptionResponse();
        dto.setId(option.getId());
        dto.setResult(option.getResult());
        dto.setOdds(option.getOdds());
        dto.setMatchId(option.getMatch().getId());
        dto.setDescription(option.getDescription());
        return dto;
    }
}

