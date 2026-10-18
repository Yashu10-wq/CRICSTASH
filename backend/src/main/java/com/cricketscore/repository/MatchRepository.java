package com.cricketscore.repository;

import com.cricketscore.entity.Match;
import com.cricketscore.entity.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Page<Match> findByStatus(MatchStatus status, Pageable pageable);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId")
    Page<Match> findByTeamId(@Param("teamId") Long teamId, Pageable pageable);

    @Query("SELECT m FROM Match m " +
           "JOIN FETCH m.homeTeam " +
           "JOIN FETCH m.awayTeam " +
           "LEFT JOIN FETCH m.tossWinnerTeam " +
           "LEFT JOIN FETCH m.winnerTeam " +
           "WHERE m.id = :id")
    Optional<Match> findByIdWithDetails(@Param("id") Long id);
}
