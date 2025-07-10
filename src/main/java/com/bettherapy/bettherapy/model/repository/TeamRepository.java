package com.bettherapy.bettherapy.model.repository;


import com.bettherapy.bettherapy.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByLeagueId(Long leagueId);

    List<Team> findByLeague_IdOrderByNameAsc(Long leagueId);
}
