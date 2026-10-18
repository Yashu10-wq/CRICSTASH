package com.cricketscore.repository;

import com.cricketscore.entity.BallEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BallEventRepository extends JpaRepository<BallEvent, Long> {
    List<BallEvent> findByInningsIdAndUndoneFalseOrderByEventSequenceAsc(Long inningsId);
    Page<BallEvent> findByInningsIdAndUndoneFalseOrderByEventSequenceDesc(Long inningsId, Pageable pageable);
    Optional<BallEvent> findTopByInningsIdAndUndoneFalseOrderByEventSequenceDesc(Long inningsId);

    @Query("SELECT COALESCE(MAX(be.eventSequence), 0) FROM BallEvent be WHERE be.innings.id = :inningsId")
    Long findMaxEventSequence(@Param("inningsId") Long inningsId);

    @Query("SELECT be FROM BallEvent be " +
           "JOIN FETCH be.striker " +
           "JOIN FETCH be.nonStriker " +
           "JOIN FETCH be.bowler " +
           "LEFT JOIN FETCH be.dismissedPlayer " +
           "LEFT JOIN FETCH be.fielder " +
           "WHERE be.innings.id = :inningsId AND be.undone = false " +
           "ORDER BY be.eventSequence ASC")
    List<BallEvent> findActiveDeliveriesWithPlayers(@Param("inningsId") Long inningsId);
}
