package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUserId(Long userId);

    List<Bet> findByMatch_Id(Long matchId);

    @Query("""
      SELECT b FROM Bet b
      JOIN FETCH b.betOption
      JOIN FETCH b.match m
      JOIN FETCH m.homeTeam
      JOIN FETCH m.awayTeam
      WHERE b.user.id = :userId
    """)
    List<Bet> findByUserIdWithMatchAndOption(@Param("userId") Long userId);


}
