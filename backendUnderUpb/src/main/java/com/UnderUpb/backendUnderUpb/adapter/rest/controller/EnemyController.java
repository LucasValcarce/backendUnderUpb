package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.EnemyService;
import com.UnderUpb.backendUnderUpb.dto.enemy.EnemyRequestDto;
import com.UnderUpb.backendUnderUpb.dto.enemy.EnemyResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enemies")
@RequiredArgsConstructor
@Tag(name = "Enemy Management", description = "Endpoints for managing game enemies and bosses")
public class EnemyController {

    private final EnemyService enemyService;

    @PostMapping
    @Operation(summary = "Create an enemy", description = "Creates a new enemy/boss")
    @ApiResponse(responseCode = "201", description = "Enemy created successfully")
    public ResponseEntity<EnemyResponseDto> createEnemy(@RequestBody EnemyRequestDto enemyDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enemyService.createEnemy(enemyDto));
    }

    @GetMapping
    @Operation(summary = "Get all enemies", description = "Retrieves all enemies with pagination")
    @ApiResponse(responseCode = "200", description = "Enemies retrieved successfully")
    public ResponseEntity<Page<EnemyResponseDto>> getAllEnemies(Pageable pageable) {
        return ResponseEntity.ok(enemyService.getAllEnemies(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enemy by ID", description = "Retrieves an enemy by its unique ID")
    @ApiResponse(responseCode = "200", description = "Enemy retrieved successfully")
    public ResponseEntity<EnemyResponseDto> getEnemyById(@PathVariable UUID id) {
        return ResponseEntity.ok(enemyService.getEnemyById(id));
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "Get enemies by level", description = "Retrieves all enemies for a specific level")
    @ApiResponse(responseCode = "200", description = "Enemies retrieved successfully")
    public ResponseEntity<List<EnemyResponseDto>> getEnemiesByLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(enemyService.getEnemiesByLevel(level));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update enemy", description = "Updates an existing enemy")
    @ApiResponse(responseCode = "200", description = "Enemy updated successfully")
    public ResponseEntity<EnemyResponseDto> updateEnemy(
            @PathVariable UUID id,
            @RequestBody EnemyRequestDto enemyDto) {
        return ResponseEntity.ok(enemyService.updateEnemy(id, enemyDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update enemy", description = "Partially updates an enemy")
    @ApiResponse(responseCode = "200", description = "Enemy updated successfully")
    public ResponseEntity<EnemyResponseDto> patchEnemy(
            @PathVariable UUID id,
            @RequestBody EnemyRequestDto enemyDto) {
        return ResponseEntity.ok(enemyService.updateEnemy(id, enemyDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete enemy", description = "Deletes an enemy from the system")
    @ApiResponse(responseCode = "204", description = "Enemy deleted successfully")
    public ResponseEntity<Void> deleteEnemy(@PathVariable UUID id) {
        enemyService.deleteEnemy(id);
        return ResponseEntity.noContent().build();
    }
}
