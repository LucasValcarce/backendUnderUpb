package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.AnswerService;
import com.UnderUpb.backendUnderUpb.dto.answer.AnswerRequestDto;
import com.UnderUpb.backendUnderUpb.dto.answer.AnswerResponseDto;
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
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
@Tag(name = "Answer Management", description = "Endpoints for managing quiz answer options")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    @Operation(summary = "Create an answer", description = "Creates a new answer option for a question")
    @ApiResponse(responseCode = "201", description = "Answer created successfully")
    public ResponseEntity<AnswerResponseDto> createAnswer(@RequestBody AnswerRequestDto answerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(answerService.createAnswer(answerDto));
    }

    @GetMapping
    @Operation(summary = "Get all answers", description = "Retrieves all answers with pagination")
    @ApiResponse(responseCode = "200", description = "Answers retrieved successfully")
    public ResponseEntity<Page<AnswerResponseDto>> getAllAnswers(Pageable pageable) {
        return ResponseEntity.ok(answerService.getAllAnswers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get answer by ID", description = "Retrieves an answer by its unique ID")
    @ApiResponse(responseCode = "200", description = "Answer retrieved successfully")
    public ResponseEntity<AnswerResponseDto> getAnswerById(@PathVariable UUID id) {
        return ResponseEntity.ok(answerService.getAnswerById(id));
    }

    @GetMapping("/questions/{questionId}")
    @Operation(summary = "Get answers for a question", description = "Retrieves all answer options for a specific question")
    @ApiResponse(responseCode = "200", description = "Answers retrieved successfully")
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByQuestion(@PathVariable UUID questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestion(questionId));
    }

    @GetMapping("/questions/{questionId}/correct")
    @Operation(summary = "Get correct answers for a question", description = "Retrieves all correct answer options for a specific question")
    @ApiResponse(responseCode = "200", description = "Correct answers retrieved successfully")
    public ResponseEntity<List<AnswerResponseDto>> getCorrectAnswersByQuestion(@PathVariable UUID questionId) {
        return ResponseEntity.ok(answerService.getCorrectAnswersByQuestion(questionId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update answer", description = "Updates an existing answer option")
    @ApiResponse(responseCode = "200", description = "Answer updated successfully")
    public ResponseEntity<AnswerResponseDto> updateAnswer(
            @PathVariable UUID id,
            @RequestBody AnswerRequestDto answerDto) {
        return ResponseEntity.ok(answerService.updateAnswer(id, answerDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update answer", description = "Partially updates an answer option")
    @ApiResponse(responseCode = "200", description = "Answer updated successfully")
    public ResponseEntity<AnswerResponseDto> patchAnswer(
            @PathVariable UUID id,
            @RequestBody AnswerRequestDto answerDto) {
        return ResponseEntity.ok(answerService.updateAnswer(id, answerDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete answer", description = "Deletes an answer option from the system")
    @ApiResponse(responseCode = "204", description = "Answer deleted successfully")
    public ResponseEntity<Void> deleteAnswer(@PathVariable UUID id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}
