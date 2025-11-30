package com.UnderUpb.backendUnderUpb.repository;

import com.UnderUpb.backendUnderUpb.entity.LeaderboardEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, UUID> {

    @Query("SELECT l FROM LeaderboardEntry l ORDER BY l.score DESC")
    Page<LeaderboardEntry> findTopEntries(Pageable pageable);

    Page<LeaderboardEntry> findByUserIdOrderByScoreDesc(UUID userId, Pageable pageable);
}
