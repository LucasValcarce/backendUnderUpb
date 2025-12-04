package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.CharacterService;
import com.UnderUpb.backendUnderUpb.dto.character.CharacterRequestDto;
import com.UnderUpb.backendUnderUpb.dto.character.CharacterResponseDto;
import com.UnderUpb.backendUnderUpb.entity.CharacterEntity;
import com.UnderUpb.backendUnderUpb.repository.CharacterRepository;
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
public class CharacterServiceImpl implements CharacterService {

    private final CharacterRepository characterRepository;

    @Override
    @Transactional
    public CharacterResponseDto createCharacter(CharacterRequestDto characterDto) {
        if (characterDto == null || characterDto.getName() == null) {
            throw new IllegalArgumentException("Character data cannot be null");
        }
        CharacterEntity character = CharacterEntity.builder()
                .name(characterDto.getName())
                .description(characterDto.getDescription())
                .build();
        CharacterEntity savedCharacter = characterRepository.save(character);
        log.info("Character created with ID: {}", savedCharacter.getId());
        return toResponseDto(savedCharacter);
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterResponseDto getCharacterById(UUID characterId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with ID: " + characterId));
        return toResponseDto(character);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CharacterResponseDto> getAllCharacters(Pageable pageable) {
        return characterRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public CharacterResponseDto updateCharacter(UUID characterId, CharacterRequestDto characterDto) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found with ID: " + characterId));
        if (characterDto.getName() != null) {
            character.setName(characterDto.getName());
        }
        if (characterDto.getDescription() != null) {
            character.setDescription(characterDto.getDescription());
        }
        CharacterEntity updatedCharacter = characterRepository.save(character);
        log.info("Character updated with ID: {}", characterId);
        return toResponseDto(updatedCharacter);
    }

    @Override
    @Transactional
    public void deleteCharacter(UUID characterId) {
        if (!characterRepository.existsById(characterId)) {
            throw new IllegalArgumentException("Character not found with ID: " + characterId);
        }
        characterRepository.deleteById(characterId);
        log.info("Character deleted with ID: {}", characterId);
    }

    private CharacterResponseDto toResponseDto(CharacterEntity character) {
        return CharacterResponseDto.builder()
                .id(character.getId())
                .name(character.getName())
                .description(character.getDescription())
                .createdDate(character.getCreatedDate())
                .updatedDate(character.getUpdatedDate())
                .build();
    }
}
