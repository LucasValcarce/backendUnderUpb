package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.DecisionService;
import com.UnderUpb.backendUnderUpb.dto.decision.DecisionRequestDto;
import com.UnderUpb.backendUnderUpb.dto.decision.DecisionResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Decision;
import com.UnderUpb.backendUnderUpb.repository.DecisionRepository;
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
public class DecisionServiceImpl implements DecisionService {

    private final DecisionRepository decisionRepository;

    @Override
    @Transactional
    public DecisionResponseDto createDecision(DecisionRequestDto decisionDto) {
        if (decisionDto == null) {
            throw new IllegalArgumentException("Decision data cannot be null");
        }
        Decision decision = Decision.builder()
                .decisionContent(decisionDto.getDecisionContent())
                .description(decisionDto.getDescription())
                .build();
        Decision savedDecision = decisionRepository.save(decision);
        log.info("Decision created with ID: {}", savedDecision.getId());
        return toResponseDto(savedDecision);
    }

    @Override
    @Transactional(readOnly = true)
    public DecisionResponseDto getDecisionById(UUID decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision not found with ID: " + decisionId));
        return toResponseDto(decision);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DecisionResponseDto> getAllDecisions(Pageable pageable) {
        return decisionRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public DecisionResponseDto updateDecision(UUID decisionId, DecisionRequestDto decisionDto) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision not found with ID: " + decisionId));
        if (decisionDto.getDecisionContent() != null) {
            decision.setDecisionContent(decisionDto.getDecisionContent());
        }
        if (decisionDto.getDescription() != null) {
            decision.setDescription(decisionDto.getDescription());
        }
        Decision updatedDecision = decisionRepository.save(decision);
        log.info("Decision updated with ID: {}", decisionId);
        return toResponseDto(updatedDecision);
    }

    @Override
    @Transactional
    public void deleteDecision(UUID decisionId) {
        if (!decisionRepository.existsById(decisionId)) {
            throw new IllegalArgumentException("Decision not found with ID: " + decisionId);
        }
        decisionRepository.deleteById(decisionId);
        log.info("Decision deleted with ID: {}", decisionId);
    }

    private DecisionResponseDto toResponseDto(Decision decision) {
        return DecisionResponseDto.builder()
                .id(decision.getId())
                .decisionContent(decision.getDecisionContent())
                .description(decision.getDescription())
                .createdDate(decision.getCreatedDate())
                .updatedDate(decision.getUpdatedDate())
                .build();
    }
}
