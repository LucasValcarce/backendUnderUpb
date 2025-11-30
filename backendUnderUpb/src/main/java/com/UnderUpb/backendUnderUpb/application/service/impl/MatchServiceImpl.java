package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.MatchService;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Integer calculateFinalScore(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> user.getScore() * user.getCurrentLevel())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    @Override
    @Transactional
    public void endMatch(UUID userId, Integer finalScore) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setScore(finalScore);
            userRepository.save(user);
            log.info("Match ended for user: {} with final score: {}", userId, finalScore);
        });
    }
}
