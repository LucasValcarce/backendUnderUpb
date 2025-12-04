package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.LeaderboardService;
import com.UnderUpb.backendUnderUpb.dto.leaderboard.LeaderboardResponseDto;
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
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard Management", description = "Endpoints for managing game leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/top")
    @Operation(summary = "Get top entries", description = "Retrieves the top players")
    @ApiResponse(responseCode = "200", description = "Top entries retrieved successfully")
    public ResponseEntity<List<LeaderboardResponseDto>> getTopEntries(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopEntries(limit));
    }

    @GetMapping
    @Operation(summary = "Get top entries paginated", description = "Retrieves top players with pagination")
    @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully")
    public ResponseEntity<Page<LeaderboardResponseDto>> getTopEntriesPaginated(Pageable pageable) {
        return ResponseEntity.ok(leaderboardService.getTopEntriesPaginated(pageable));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user scores", description = "Retrieves all scores for a specific user")
    @ApiResponse(responseCode = "200", description = "User scores retrieved successfully")
    public ResponseEntity<Page<LeaderboardResponseDto>> getUserScores(
            @PathVariable UUID userId,
            Pageable pageable) {
        return ResponseEntity.ok(leaderboardService.getUserScores(userId, pageable));
    }

    @PostMapping("/users/{userId}/score")
    @Operation(summary = "Record score", description = "Records a new score for a user")
    @ApiResponse(responseCode = "201", description = "Score recorded successfully")
    public ResponseEntity<LeaderboardResponseDto> recordScore(
            @PathVariable UUID userId,
            @RequestParam Integer score) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaderboardService.recordScore(userId, score));
    }
}
