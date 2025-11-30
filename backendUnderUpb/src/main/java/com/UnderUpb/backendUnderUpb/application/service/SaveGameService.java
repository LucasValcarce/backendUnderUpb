package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.savegame.SaveGameRequestDto;
import com.UnderUpb.backendUnderUpb.dto.savegame.SaveGameResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SaveGameService {
    SaveGameResponseDto saveGame(SaveGameRequestDto saveGameDto);
    SaveGameResponseDto getLatestSave(UUID userId);
    Page<SaveGameResponseDto> getAllSavesByUser(UUID userId, Pageable pageable);
    SaveGameResponseDto updateSave(UUID saveId, SaveGameRequestDto saveGameDto);
    void deleteSave(UUID saveId);
    boolean validateSaveSignature(UUID saveId);
}
