package com.cricketscore.repository;

import com.cricketscore.entity.MatchSquad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchSquadRepository extends JpaRepository<MatchSquad, Long> {
    List<MatchSquad> findByMatchId(Long matchId);
    List<MatchSquad> findByMatchIdAndTeamId(Long matchId, Long teamId);
    List<MatchSquad> findByMatchIdAndTeamIdAndPlayingXiTrue(Long matchId, Long teamId);
    Optional<MatchSquad> findByMatchIdAndPlayerId(Long matchId, Long playerId);
    boolean existsByMatchIdAndPlayerId(Long matchId, Long playerId);

    @Query("SELECT ms FROM MatchSquad ms JOIN FETCH ms.player WHERE ms.match.id = :matchId AND ms.team.id = :teamId")
    List<MatchSquad> findByMatchIdAndTeamIdWithPlayer(@Param("matchId") Long matchId, @Param("teamId") Long teamId);
}
