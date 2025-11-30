package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.DecisionService;
import com.UnderUpb.backendUnderUpb.dto.decision.DecisionRequestDto;
import com.UnderUpb.backendUnderUpb.dto.decision.DecisionResponseDto;
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
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
@Tag(name = "Decision Management", description = "Endpoints for managing game decisions and dialogue trees")
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping
    @Operation(summary = "Create a decision", description = "Creates a new decision/dialogue option")
    @ApiResponse(responseCode = "201", description = "Decision created successfully")
    public ResponseEntity<DecisionResponseDto> createDecision(@RequestBody DecisionRequestDto decisionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(decisionService.createDecision(decisionDto));
    }

    @GetMapping
    @Operation(summary = "Get all decisions", description = "Retrieves all decisions with pagination")
    @ApiResponse(responseCode = "200", description = "Decisions retrieved successfully")
    public ResponseEntity<Page<DecisionResponseDto>> getAllDecisions(Pageable pageable) {
        return ResponseEntity.ok(decisionService.getAllDecisions(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get decision by ID", description = "Retrieves a decision by its unique ID")
    @ApiResponse(responseCode = "200", description = "Decision retrieved successfully")
    public ResponseEntity<DecisionResponseDto> getDecisionById(@PathVariable UUID id) {
        return ResponseEntity.ok(decisionService.getDecisionById(id));
    }

    @GetMapping("/questions/{questionId}")
    @Operation(summary = "Get decisions by question", description = "Retrieves all decisions for a specific question")
    @ApiResponse(responseCode = "200", description = "Decisions retrieved successfully")
    public ResponseEntity<List<DecisionResponseDto>> getDecisionsByQuestion(@PathVariable UUID questionId) {
        return ResponseEntity.ok(decisionService.getDecisionsByQuestion(questionId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update decision", description = "Updates an existing decision")
    @ApiResponse(responseCode = "200", description = "Decision updated successfully")
    public ResponseEntity<DecisionResponseDto> updateDecision(
            @PathVariable UUID id,
            @RequestBody DecisionRequestDto decisionDto) {
        return ResponseEntity.ok(decisionService.updateDecision(id, decisionDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update decision", description = "Partially updates a decision")
    @ApiResponse(responseCode = "200", description = "Decision updated successfully")
    public ResponseEntity<DecisionResponseDto> patchDecision(
            @PathVariable UUID id,
            @RequestBody DecisionRequestDto decisionDto) {
        return ResponseEntity.ok(decisionService.updateDecision(id, decisionDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete decision", description = "Deletes a decision from the system")
    @ApiResponse(responseCode = "204", description = "Decision deleted successfully")
    public ResponseEntity<Void> deleteDecision(@PathVariable UUID id) {
        decisionService.deleteDecision(id);
        return ResponseEntity.noContent().build();
    }
}
