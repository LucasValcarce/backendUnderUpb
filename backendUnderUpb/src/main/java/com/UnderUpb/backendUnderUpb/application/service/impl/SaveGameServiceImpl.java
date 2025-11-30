package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.SaveGameService;
import com.UnderUpb.backendUnderUpb.dto.savegame.SaveGameRequestDto;
import com.UnderUpb.backendUnderUpb.dto.savegame.SaveGameResponseDto;
import com.UnderUpb.backendUnderUpb.entity.SaveGame;
import com.UnderUpb.backendUnderUpb.repository.SaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveGameServiceImpl implements SaveGameService {

    private final SaveRepository saveRepository;

    @Override
    @Transactional
    public SaveGameResponseDto saveGame(SaveGameRequestDto saveGameDto) {
        if (saveGameDto == null || saveGameDto.getUserId() == null) {
            throw new IllegalArgumentException("Save game data cannot be null");
        }
        SaveGame saveGame = SaveGame.builder()
                .userId(saveGameDto.getUserId())
                .stateJson(saveGameDto.getStateJson())
                .version(saveGameDto.getVersion() != null ? saveGameDto.getVersion() : 1)
                .build();
        SaveGame savedGame = saveRepository.save(saveGame);
        log.info("Save game created with ID: {}", savedGame.getId());
        return toResponseDto(savedGame);
    }

    @Override
    @Transactional(readOnly = true)
    public SaveGameResponseDto getLatestSave(UUID userId) {
        SaveGame saveGame = saveRepository.findByUserIdOrderByUpdatedDateDesc(userId).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No save game found for user: " + userId));
        return toResponseDto(saveGame);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaveGameResponseDto> getAllSavesByUser(UUID userId, Pageable pageable) {
        List<SaveGame> saves = saveRepository.findByUserIdOrderByUpdatedDateDesc(userId);
        List<SaveGameResponseDto> dtos = saves.stream()
                .map(this::toResponseDto)
                .toList();
        int start = Math.min((int)pageable.getOffset(), dtos.size());
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    @Transactional
    public SaveGameResponseDto updateSave(UUID saveId, SaveGameRequestDto saveGameDto) {
        SaveGame saveGame = saveRepository.findById(saveId)
                .orElseThrow(() -> new IllegalArgumentException("Save game not found with ID: " + saveId));
        if (saveGameDto.getStateJson() != null) {
            saveGame.setStateJson(saveGameDto.getStateJson());
        }
        if (saveGameDto.getVersion() != null) {
            saveGame.setVersion(saveGameDto.getVersion());
        }
        SaveGame updatedGame = saveRepository.save(saveGame);
        log.info("Save game updated with ID: {}", saveId);
        return toResponseDto(updatedGame);
    }

    @Override
    @Transactional
    public void deleteSave(UUID saveId) {
        if (!saveRepository.existsById(saveId)) {
            throw new IllegalArgumentException("Save game not found with ID: " + saveId);
        }
        saveRepository.deleteById(saveId);
        log.info("Save game deleted with ID: {}", saveId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateSaveSignature(UUID saveId) {
        return saveRepository.existsById(saveId);
    }

    private SaveGameResponseDto toResponseDto(SaveGame saveGame) {
        return SaveGameResponseDto.builder()
                .id(saveGame.getId())
                .userId(saveGame.getUserId())
                .stateJson(saveGame.getStateJson())
                .version(saveGame.getVersion())
                .createdDate(saveGame.getCreatedDate())
                .updatedDate(saveGame.getUpdatedDate())
                .build();
    }
}
