package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Tag(name = "Match Management", description = "Endpoints for managing game matches and scoring")
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/users/{userId}/score")
    @Operation(summary = "Calculate final score", description = "Calculates the final score for a user")
    @ApiResponse(responseCode = "200", description = "Score calculated successfully")
    public ResponseEntity<Integer> calculateFinalScore(@PathVariable UUID userId) {
        return ResponseEntity.ok(matchService.calculateFinalScore(userId));
    }

    @PostMapping("/users/{userId}/end")
    @Operation(summary = "End match", description = "Ends a match for a user and records the final score")
    @ApiResponse(responseCode = "200", description = "Match ended successfully")
    public ResponseEntity<Void> endMatch(
            @PathVariable UUID userId,
            @RequestParam Integer finalScore) {
        matchService.endMatch(userId, finalScore);
        return ResponseEntity.ok().build();
    }
}
