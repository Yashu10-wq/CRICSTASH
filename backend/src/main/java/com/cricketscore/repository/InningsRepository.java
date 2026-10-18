package com.cricketscore.repository;

import com.cricketscore.entity.Innings;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InningsRepository extends JpaRepository<Innings, Long> {
    List<Innings> findByMatchIdOrderByInningsNumberAsc(Long matchId);
    Optional<Innings> findByMatchIdAndInningsNumber(Long matchId, Integer inningsNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Innings i WHERE i.id = :id")
    Optional<Innings> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT i FROM Innings i " +
           "JOIN FETCH i.match m " +
           "JOIN FETCH i.battingTeam " +
           "JOIN FETCH i.bowlingTeam " +
           "LEFT JOIN FETCH i.currentStriker " +
           "LEFT JOIN FETCH i.currentNonStriker " +
           "LEFT JOIN FETCH i.currentBowler " +
           "WHERE i.id = :id")
    Optional<Innings> findByIdWithDetails(@Param("id") Long id);
}
