package com.cricketscore.repository;

import com.cricketscore.entity.Player;
import com.cricketscore.entity.enums.PlayerRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Page<Player> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
    Page<Player> findByPrimaryRole(PlayerRole primaryRole, Pageable pageable);
    List<Player> findByIdIn(List<Long> ids);
}
