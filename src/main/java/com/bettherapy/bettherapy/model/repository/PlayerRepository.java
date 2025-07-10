package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTeamId(Long teamId);

    List<Player> findByCountry_Name(String nationality);
}
