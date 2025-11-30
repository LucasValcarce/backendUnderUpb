package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.character.CharacterRequestDto;
import com.UnderUpb.backendUnderUpb.dto.character.CharacterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CharacterService {
    CharacterResponseDto createCharacter(CharacterRequestDto characterDto);
    CharacterResponseDto getCharacterById(UUID characterId);
    Page<CharacterResponseDto> getAllCharacters(Pageable pageable);
    CharacterResponseDto updateCharacter(UUID characterId, CharacterRequestDto characterDto);
    void deleteCharacter(UUID characterId);
}
