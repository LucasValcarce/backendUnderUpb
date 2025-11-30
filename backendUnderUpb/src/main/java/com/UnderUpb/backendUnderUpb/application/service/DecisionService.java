package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.decision.DecisionRequestDto;
import com.UnderUpb.backendUnderUpb.dto.decision.DecisionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DecisionService {
    DecisionResponseDto createDecision(DecisionRequestDto decisionDto);
    DecisionResponseDto getDecisionById(UUID decisionId);
    Page<DecisionResponseDto> getAllDecisions(Pageable pageable);
    DecisionResponseDto updateDecision(UUID decisionId, DecisionRequestDto decisionDto);
    void deleteDecision(UUID decisionId);
    List<DecisionResponseDto> getDecisionsByQuestion(UUID questionId);
}
