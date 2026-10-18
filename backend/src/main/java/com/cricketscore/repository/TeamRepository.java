package com.cricketscore.repository;

import com.cricketscore.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    Page<Team> findByNameContainingIgnoreCaseOrCountryContainingIgnoreCase(String name, String country, Pageable pageable);
}
