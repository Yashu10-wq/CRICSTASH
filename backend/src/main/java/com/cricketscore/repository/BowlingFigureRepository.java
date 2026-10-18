package com.cricketscore.repository;

import com.cricketscore.entity.BowlingFigure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BowlingFigureRepository extends JpaRepository<BowlingFigure, Long> {
    List<BowlingFigure> findByInningsId(Long inningsId);
    Optional<BowlingFigure> findByInningsIdAndPlayerId(Long inningsId, Long playerId);

    @Query("SELECT bf FROM BowlingFigure bf JOIN FETCH bf.player WHERE bf.innings.id = :inningsId")
    List<BowlingFigure> findByInningsIdWithPlayerDetails(@Param("inningsId") Long inningsId);

    @Query("SELECT COUNT(bf) FROM BowlingFigure bf WHERE bf.player.id = :playerId AND bf.legalBallsBowled > 0")
    long countInningsBowledByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bf.legalBallsBowled), 0) FROM BowlingFigure bf WHERE bf.player.id = :playerId")
    long sumBallsBowledByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bf.runsConceded), 0) FROM BowlingFigure bf WHERE bf.player.id = :playerId")
    long sumRunsConcededByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bf.wickets), 0) FROM BowlingFigure bf WHERE bf.player.id = :playerId")
    long sumWicketsByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bf.maidens), 0) FROM BowlingFigure bf WHERE bf.player.id = :playerId")
    long sumMaidensByPlayerId(@Param("playerId") Long playerId);
}
