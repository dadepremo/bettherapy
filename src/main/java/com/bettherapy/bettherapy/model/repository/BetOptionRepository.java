package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.BetOption;
import com.bettherapy.bettherapy.model.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetOptionRepository extends JpaRepository<BetOption, Long> {
    List<BetOption> findByMatchId(Long matchId);

    List<BetOption> findByMatch(Match match);
}
