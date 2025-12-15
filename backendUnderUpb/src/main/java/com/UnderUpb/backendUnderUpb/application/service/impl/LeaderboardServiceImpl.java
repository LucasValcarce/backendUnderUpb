package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.LeaderboardService;
import com.UnderUpb.backendUnderUpb.dto.leaderboard.LeaderboardResponseDto;
import com.UnderUpb.backendUnderUpb.entity.User;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardResponseDto> getTopEntries(int limit) {
        var pageRequest = org.springframework.data.domain.PageRequest.of(0, Math.max(1, limit), org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Order.desc("score")));
        return userRepository.findAll(pageRequest).getContent().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
        public Page<LeaderboardResponseDto> getTopEntriesPaginated(Pageable pageable) {
        var p = org.springframework.data.domain.PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Order.desc("score"))
        );
        return userRepository.findAll(p)
            .map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaderboardResponseDto> getUserScores(UUID userId, Pageable pageable) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return org.springframework.data.domain.Page.empty(pageable);
        }
        var dto = toResponseDto(userOpt.get());
        java.util.List<LeaderboardResponseDto> list = java.util.Collections.singletonList(dto);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, 1);
    }

    @Override
    @Transactional
    public LeaderboardResponseDto recordScore(UUID userId, Integer score) {
        if (userId == null || score == null) {
            throw new IllegalArgumentException("User ID and score cannot be null");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new java.lang.IllegalArgumentException("User not found"));
        // Update score if higher than current score, otherwise keep current
        if (user.getScore() == null || score > user.getScore()) {
            user.setScore(score);
            user = userRepository.save(user);
            log.info("User {} score updated to {}", userId, score);
        } else {
            log.debug("Received lower score for user {}: {} (current {})", userId, score, user.getScore());
        }
        return toResponseDto(user);
    }

    private LeaderboardResponseDto toResponseDto(User user) {
        return LeaderboardResponseDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .score(user.getScore())
                .build();
    }
}
