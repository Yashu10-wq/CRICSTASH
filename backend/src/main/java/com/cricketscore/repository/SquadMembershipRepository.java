package com.cricketscore.repository;

import com.cricketscore.entity.SquadMembership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SquadMembershipRepository extends JpaRepository<SquadMembership, Long> {
    List<SquadMembership> findByTeamIdAndActiveTrue(Long teamId);
    Page<SquadMembership> findByTeamId(Long teamId, Pageable pageable);
    Optional<SquadMembership> findByTeamIdAndPlayerId(Long teamId, Long playerId);
    boolean existsByTeamIdAndPlayerId(Long teamId, Long playerId);

    @Query("SELECT sm FROM SquadMembership sm JOIN FETCH sm.player WHERE sm.team.id = :teamId AND sm.active = true")
    List<SquadMembership> findActiveWithPlayerDetails(@Param("teamId") Long teamId);
}
