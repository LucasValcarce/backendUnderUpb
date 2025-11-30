package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.CharacterService;
import com.UnderUpb.backendUnderUpb.dto.character.CharacterRequestDto;
import com.UnderUpb.backendUnderUpb.dto.character.CharacterResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
@Tag(name = "Character Management", description = "Endpoints for managing game characters")
public class CharacterController {

    private final CharacterService characterService;

    @PostMapping
    @Operation(summary = "Create a new character", description = "Creates a new game character")
    @ApiResponse(responseCode = "201", description = "Character created successfully")
    public ResponseEntity<CharacterResponseDto> createCharacter(@RequestBody CharacterRequestDto characterDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(characterService.createCharacter(characterDto));
    }

    @GetMapping
    @Operation(summary = "Get all characters", description = "Retrieves all characters with pagination")
    @ApiResponse(responseCode = "200", description = "Characters retrieved successfully")
    public ResponseEntity<Page<CharacterResponseDto>> getAllCharacters(Pageable pageable) {
        return ResponseEntity.ok(characterService.getAllCharacters(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get character by ID", description = "Retrieves a character by its unique ID")
    @ApiResponse(responseCode = "200", description = "Character retrieved successfully")
    public ResponseEntity<CharacterResponseDto> getCharacterById(@PathVariable UUID id) {
        return ResponseEntity.ok(characterService.getCharacterById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update character", description = "Updates an existing character")
    @ApiResponse(responseCode = "200", description = "Character updated successfully")
    public ResponseEntity<CharacterResponseDto> updateCharacter(
            @PathVariable UUID id,
            @RequestBody CharacterRequestDto characterDto) {
        return ResponseEntity.ok(characterService.updateCharacter(id, characterDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update character", description = "Partially updates a character")
    @ApiResponse(responseCode = "200", description = "Character updated successfully")
    public ResponseEntity<CharacterResponseDto> patchCharacter(
            @PathVariable UUID id,
            @RequestBody CharacterRequestDto characterDto) {
        return ResponseEntity.ok(characterService.updateCharacter(id, characterDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete character", description = "Deletes a character from the system")
    @ApiResponse(responseCode = "204", description = "Character deleted successfully")
    public ResponseEntity<Void> deleteCharacter(@PathVariable UUID id) {
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }
}
