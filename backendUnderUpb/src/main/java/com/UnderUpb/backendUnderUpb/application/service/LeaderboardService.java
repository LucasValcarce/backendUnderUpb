package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.leaderboard.LeaderboardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LeaderboardService {
    List<LeaderboardResponseDto> getTopEntries(int limit);
    Page<LeaderboardResponseDto> getTopEntriesPaginated(Pageable pageable);
    Page<LeaderboardResponseDto> getUserScores(UUID userId, Pageable pageable);
    LeaderboardResponseDto recordScore(UUID userId, Integer score);
}
