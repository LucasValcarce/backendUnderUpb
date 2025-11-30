package com.UnderUpb.backendUnderUpb.application.service;

import java.util.UUID;

public interface MatchService {
    Integer calculateFinalScore(UUID userId);
    void endMatch(UUID userId, Integer finalScore);
}
