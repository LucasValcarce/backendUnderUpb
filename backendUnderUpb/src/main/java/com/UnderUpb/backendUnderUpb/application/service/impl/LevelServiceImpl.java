package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.LevelService;
import com.UnderUpb.backendUnderUpb.dto.level.LevelRequestDto;
import com.UnderUpb.backendUnderUpb.dto.level.LevelResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Level;
import com.UnderUpb.backendUnderUpb.entity.User;
import com.UnderUpb.backendUnderUpb.repository.LevelRepository;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LevelResponseDto createLevel(LevelRequestDto levelDto) {
        if (levelDto == null || levelDto.getName() == null) {
            throw new IllegalArgumentException("Level data cannot be null");
        }
        Level level = Level.builder()
                .name(levelDto.getName())
                .description(levelDto.getDescription())
                .requiredXp(levelDto.getRequiredXp())
                .orderIndex(levelDto.getOrderIndex())
                .build();
        Level savedLevel = levelRepository.save(level);
        log.info("Level created with ID: {}", savedLevel.getId());
        return toResponseDto(savedLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public LevelResponseDto getLevelById(UUID levelId) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new IllegalArgumentException("Level not found with ID: " + levelId));
        return toResponseDto(level);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LevelResponseDto> getAllLevels(Pageable pageable) {
        return levelRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public LevelResponseDto updateLevel(UUID levelId, LevelRequestDto levelDto) {
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new IllegalArgumentException("Level not found with ID: " + levelId));
        if (levelDto.getName() != null) {
            level.setName(levelDto.getName());
        }
        if (levelDto.getDescription() != null) {
            level.setDescription(levelDto.getDescription());
        }
        if (levelDto.getRequiredXp() != null) {
            level.setRequiredXp(levelDto.getRequiredXp());
        }
        if (levelDto.getOrderIndex() != null) {
            level.setOrderIndex(levelDto.getOrderIndex());
        }
        Level updatedLevel = levelRepository.save(level);
        log.info("Level updated with ID: {}", levelId);
        return toResponseDto(updatedLevel);
    }

    @Override
    @Transactional
    public void deleteLevel(UUID levelId) {
        if (!levelRepository.existsById(levelId)) {
            throw new IllegalArgumentException("Level not found with ID: " + levelId);
        }
        levelRepository.deleteById(levelId);
        log.info("Level deleted with ID: {}", levelId);
    }

    @Override
    @Transactional
    public LevelResponseDto levelUpUser(UUID userId, Integer levelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.setCurrentLevel(levelId);
        userRepository.save(user);
        log.info("User {} leveled up to level {}", userId, levelId);
        return getLevelById(UUID.fromString(levelId.toString()));
    }

    private LevelResponseDto toResponseDto(Level level) {
        return LevelResponseDto.builder()
                .id(level.getId())
                .name(level.getName())
                .description(level.getDescription())
                .requiredXp(level.getRequiredXp())
                .orderIndex(level.getOrderIndex())
                .createdDate(level.getCreatedDate())
                .updatedDate(level.getUpdatedDate())
                .build();
    }
}
