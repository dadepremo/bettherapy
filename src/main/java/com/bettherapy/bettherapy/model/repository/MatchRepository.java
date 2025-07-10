package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.League;
import com.bettherapy.bettherapy.model.entity.Match;
import com.bettherapy.bettherapy.util.MatchStatus;
import com.bettherapy.bettherapy.util.MatchType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByLeagueId(Long leagueId);

    void deleteAllByLeague(League league);

    List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    List<Match> findByLeague_IdAndStatus(Long leagueId, MatchStatus status);

    @Query("""
    SELECT m FROM Match m
    WHERE (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId)
      AND m.status = 'COMPLETED'
    ORDER BY m.kickoff DESC
""")
    List<Match> findRecentByTeamId(@Param("teamId") Long teamId, Pageable pageable);

    List<Match> findByLeagueIdAndType(Long leagueId, MatchType type);

    List<Match> findByLeagueIdAndGroupName(Long leagueId, String groupName);

    List<Match> findByLeagueIdAndStageLabel(Long leagueId, String stageLabel);

    List<Match> findByLeagueIdAndTypeOrderByRoundOrderAsc(Long leagueId, MatchType type);

    List<Match> findTop8ByStatusOrderByKickoffAsc(MatchStatus status, Pageable pageable);

}
