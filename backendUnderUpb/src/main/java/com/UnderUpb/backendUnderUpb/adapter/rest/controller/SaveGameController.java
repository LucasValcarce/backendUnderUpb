package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.SaveGameService;
import com.UnderUpb.backendUnderUpb.dto.savegame.SaveGameRequestDto;
import com.UnderUpb.backendUnderUpb.dto.savegame.SaveGameResponseDto;
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
@RequestMapping("/api/v1/save")
@RequiredArgsConstructor
@Tag(name = "Save Game Management", description = "Endpoints for managing game saves")
public class SaveGameController {

    private final SaveGameService saveGameService;

    @PostMapping
    @Operation(summary = "Create a save game", description = "Saves the current game state")
    @ApiResponse(responseCode = "201", description = "Game saved successfully")
    public ResponseEntity<SaveGameResponseDto> saveGame(@RequestBody SaveGameRequestDto saveGameDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saveGameService.saveGame(saveGameDto));
    }

    @GetMapping("/users/{userId}/latest")
    @Operation(summary = "Get latest save for user", description = "Retrieves the most recent save for a user")
    @ApiResponse(responseCode = "200", description = "Save retrieved successfully")
    public ResponseEntity<SaveGameResponseDto> getLatestSave(@PathVariable UUID userId) {
        return ResponseEntity.ok(saveGameService.getLatestSave(userId));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get all saves for user", description = "Retrieves all saves for a user with pagination")
    @ApiResponse(responseCode = "200", description = "Saves retrieved successfully")
    public ResponseEntity<Page<SaveGameResponseDto>> getAllSavesByUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        return ResponseEntity.ok(saveGameService.getAllSavesByUser(userId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update save game", description = "Updates an existing save game")
    @ApiResponse(responseCode = "200", description = "Save updated successfully")
    public ResponseEntity<SaveGameResponseDto> updateSave(
            @PathVariable UUID id,
            @RequestBody SaveGameRequestDto saveGameDto) {
        return ResponseEntity.ok(saveGameService.updateSave(id, saveGameDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update save game", description = "Partially updates a save game")
    @ApiResponse(responseCode = "200", description = "Save updated successfully")
    public ResponseEntity<SaveGameResponseDto> patchSave(
            @PathVariable UUID id,
            @RequestBody SaveGameRequestDto saveGameDto) {
        return ResponseEntity.ok(saveGameService.updateSave(id, saveGameDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete save game", description = "Deletes a save game")
    @ApiResponse(responseCode = "204", description = "Save deleted successfully")
    public ResponseEntity<Void> deleteSave(@PathVariable UUID id) {
        saveGameService.deleteSave(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Validate save signature", description = "Validates the integrity of a save game")
    @ApiResponse(responseCode = "200", description = "Validation result")
    public ResponseEntity<Boolean> validateSave(@PathVariable UUID id) {
        return ResponseEntity.ok(saveGameService.validateSaveSignature(id));
    }
}
