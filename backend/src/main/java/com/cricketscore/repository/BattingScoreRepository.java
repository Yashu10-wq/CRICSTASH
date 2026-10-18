package com.cricketscore.repository;

import com.cricketscore.entity.BattingScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BattingScoreRepository extends JpaRepository<BattingScore, Long> {
    List<BattingScore> findByInningsIdOrderByBattingPositionAsc(Long inningsId);
    Optional<BattingScore> findByInningsIdAndPlayerId(Long inningsId, Long playerId);
    Optional<BattingScore> findByInningsIdAndOnStrikeTrue(Long inningsId);

    @Query("SELECT bs FROM BattingScore bs " +
           "JOIN FETCH bs.player " +
           "LEFT JOIN FETCH bs.dismissedByBowler " +
           "LEFT JOIN FETCH bs.assistedByPlayer " +
           "WHERE bs.innings.id = :inningsId " +
           "ORDER BY bs.battingPosition ASC")
    List<BattingScore> findByInningsIdWithPlayerDetails(@Param("inningsId") Long inningsId);

    @Query("SELECT MAX(bs.battingPosition) FROM BattingScore bs WHERE bs.innings.id = :inningsId")
    Integer findMaxBattingPosition(@Param("inningsId") Long inningsId);

    @Query("SELECT COUNT(bs) FROM BattingScore bs WHERE bs.player.id = :playerId")
    long countInningsBattedByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bs.runs), 0) FROM BattingScore bs WHERE bs.player.id = :playerId")
    long sumRunsByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bs.ballsFaced), 0) FROM BattingScore bs WHERE bs.player.id = :playerId")
    long sumBallsFacedByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bs.fours), 0) FROM BattingScore bs WHERE bs.player.id = :playerId")
    long sumFoursByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(SUM(bs.sixes), 0) FROM BattingScore bs WHERE bs.player.id = :playerId")
    long sumSixesByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COALESCE(MAX(bs.runs), 0) FROM BattingScore bs WHERE bs.player.id = :playerId")
    int findHighestScoreByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT COUNT(bs) FROM BattingScore bs WHERE bs.player.id = :playerId AND bs.dismissalType != com.cricketscore.entity.enums.DismissalType.NOT_OUT")
    long countDismissalsByPlayerId(@Param("playerId") Long playerId);
}
