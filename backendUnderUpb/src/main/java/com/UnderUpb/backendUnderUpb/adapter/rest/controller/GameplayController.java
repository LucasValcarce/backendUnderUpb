package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.GameplayService;
import com.UnderUpb.backendUnderUpb.dto.gameplay.LevelCompletionDto;
import com.UnderUpb.backendUnderUpb.dto.gameplay.LevelCompletionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gameplay")
@RequiredArgsConstructor
@Tag(name = "Gameplay", description = "Endpoints for game mechanics and level completion")
public class GameplayController {

    private final GameplayService gameplayService;

    @PostMapping("/complete-level")
    @Operation(summary = "Complete a level", description = "Submits level completion with answers and updates user score/level")
    @ApiResponse(responseCode = "200", description = "Level completion processed successfully")
    public ResponseEntity<LevelCompletionResponseDto> completeLevel(
            @RequestBody LevelCompletionDto completionDto) {
        return ResponseEntity.ok(gameplayService.completeLevel(completionDto));
    }
}
