package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.LeaderboardService;
import com.UnderUpb.backendUnderUpb.dto.leaderboard.LeaderboardResponseDto;
import com.UnderUpb.backendUnderUpb.entity.LeaderboardEntry;
import com.UnderUpb.backendUnderUpb.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardResponseDto> getTopEntries(int limit) {
        return leaderboardRepository.findAll().stream()
                .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
                .limit(limit)
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaderboardResponseDto> getTopEntriesPaginated(Pageable pageable) {
        return leaderboardRepository.findTopEntries(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaderboardResponseDto> getUserScores(UUID userId, Pageable pageable) {
        return leaderboardRepository.findByUserIdOrderByScoreDesc(userId, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public LeaderboardResponseDto recordScore(UUID userId, Integer score) {
        if (userId == null || score == null) {
            throw new IllegalArgumentException("User ID and score cannot be null");
        }
        LeaderboardEntry entry = LeaderboardEntry.builder()
                .userId(userId)
                .score(score)
                .build();
        LeaderboardEntry savedEntry = leaderboardRepository.save(entry);
        log.info("Score recorded for user: {} with score: {}", userId, score);
        return toResponseDto(savedEntry);
    }

    private LeaderboardResponseDto toResponseDto(LeaderboardEntry entry) {
        return LeaderboardResponseDto.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .score(entry.getScore())
                .createdDate(entry.getCreatedDate())
                .updatedDate(entry.getUpdatedDate())
                .build();
    }
}
