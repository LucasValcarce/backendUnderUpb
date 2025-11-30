package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.level.LevelRequestDto;
import com.UnderUpb.backendUnderUpb.dto.level.LevelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LevelService {
    LevelResponseDto createLevel(LevelRequestDto levelDto);
    LevelResponseDto getLevelById(UUID levelId);
    Page<LevelResponseDto> getAllLevels(Pageable pageable);
    LevelResponseDto updateLevel(UUID levelId, LevelRequestDto levelDto);
    void deleteLevel(UUID levelId);
    LevelResponseDto levelUpUser(UUID userId, Integer levelId);
}
