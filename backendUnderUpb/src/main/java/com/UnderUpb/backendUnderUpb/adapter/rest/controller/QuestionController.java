package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.QuestionService;
import com.UnderUpb.backendUnderUpb.dto.question.QuestionRequestDto;
import com.UnderUpb.backendUnderUpb.dto.question.QuestionResponseDto;
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
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Question Management", description = "Endpoints for managing quiz questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "Create a new question", description = "Creates a new quiz question")
    @ApiResponse(responseCode = "201", description = "Question created successfully")
    public ResponseEntity<QuestionResponseDto> createQuestion(@RequestBody QuestionRequestDto questionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(questionDto));
    }

    @GetMapping
    @Operation(summary = "Get all questions", description = "Retrieves all questions with pagination")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<Page<QuestionResponseDto>> getAllQuestions(Pageable pageable) {
        return ResponseEntity.ok(questionService.getAllQuestions(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID", description = "Retrieves a question by its unique ID")
    @ApiResponse(responseCode = "200", description = "Question retrieved successfully")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable UUID id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "Get questions by level", description = "Retrieves questions for a specific level. Use ?count=N for N random questions")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByLevel(
            @PathVariable Integer level,
            @RequestParam(name = "count", required = false) Integer count) {
        if (count == null) {
            return ResponseEntity.ok(questionService.getQuestionsByLevel(level));
        }
        return ResponseEntity.ok(questionService.getRandomQuestionsByLevel(level, count));
    }

    @GetMapping("/levelId/{levelId}")
    @Operation(summary = "Get questions by level ID", description = "Retrieves questions for a specific level by ID")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByLevelId(
            @PathVariable UUID levelId) {
        return ResponseEntity.ok(questionService.getQuestionsByLevelId(levelId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update question", description = "Updates an existing question")
    @ApiResponse(responseCode = "200", description = "Question updated successfully")
    public ResponseEntity<QuestionResponseDto> updateQuestion(
            @PathVariable UUID id,
            @RequestBody QuestionRequestDto questionDto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update question", description = "Partially updates a question")
    @ApiResponse(responseCode = "200", description = "Question updated successfully")
    public ResponseEntity<QuestionResponseDto> patchQuestion(
            @PathVariable UUID id,
            @RequestBody QuestionRequestDto questionDto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete question", description = "Deletes a question from the system")
    @ApiResponse(responseCode = "204", description = "Question deleted successfully")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
