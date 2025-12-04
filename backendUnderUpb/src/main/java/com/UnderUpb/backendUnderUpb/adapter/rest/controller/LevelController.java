package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.LevelService;
import com.UnderUpb.backendUnderUpb.dto.level.LevelRequestDto;
import com.UnderUpb.backendUnderUpb.dto.level.LevelResponseDto;
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
@RequestMapping("/api/v1/levels")
@RequiredArgsConstructor
@Tag(name = "Level Management", description = "Endpoints for managing game levels")
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    @Operation(summary = "Create a new level", description = "Creates a new game level")
    @ApiResponse(responseCode = "201", description = "Level created successfully")
    public ResponseEntity<LevelResponseDto> createLevel(@RequestBody LevelRequestDto levelDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(levelService.createLevel(levelDto));
    }

    @GetMapping
    @Operation(summary = "Get all levels", description = "Retrieves all levels with pagination")
    @ApiResponse(responseCode = "200", description = "Levels retrieved successfully")
    public ResponseEntity<Page<LevelResponseDto>> getAllLevels(Pageable pageable) {
        return ResponseEntity.ok(levelService.getAllLevels(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get level by ID", description = "Retrieves a level by its unique ID")
    @ApiResponse(responseCode = "200", description = "Level retrieved successfully")
    public ResponseEntity<LevelResponseDto> getLevelById(@PathVariable UUID id) {
        return ResponseEntity.ok(levelService.getLevelById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update level", description = "Updates an existing level")
    @ApiResponse(responseCode = "200", description = "Level updated successfully")
    public ResponseEntity<LevelResponseDto> updateLevel(
            @PathVariable UUID id,
            @RequestBody LevelRequestDto levelDto) {
        return ResponseEntity.ok(levelService.updateLevel(id, levelDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update level", description = "Partially updates a level")
    @ApiResponse(responseCode = "200", description = "Level updated successfully")
    public ResponseEntity<LevelResponseDto> patchLevel(
            @PathVariable UUID id,
            @RequestBody LevelRequestDto levelDto) {
        return ResponseEntity.ok(levelService.updateLevel(id, levelDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete level", description = "Deletes a level from the system")
    @ApiResponse(responseCode = "204", description = "Level deleted successfully")
    public ResponseEntity<Void> deleteLevel(@PathVariable UUID id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{levelId}/users/{userId}/levelup")
    @Operation(summary = "Level up user", description = "Advances a user to a specific level")
    @ApiResponse(responseCode = "200", description = "User leveled up successfully")
    public ResponseEntity<LevelResponseDto> levelUpUser(
            @PathVariable UUID userId,
            @PathVariable UUID levelId) {
        return ResponseEntity.ok(levelService.levelUpUser(userId, levelId.hashCode()));
    }
}
