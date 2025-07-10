package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.LeagueStanding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LeagueStandingRepository extends JpaRepository<LeagueStanding, Long> {

    List<LeagueStanding> findByLeagueIdOrderByGroupNameAscPointsDescGoalDifferenceDescGoalsForDesc(Long leagueId);

    Optional<LeagueStanding> findByLeagueIdAndTeamId(Long leagueId, Long teamId);

}

