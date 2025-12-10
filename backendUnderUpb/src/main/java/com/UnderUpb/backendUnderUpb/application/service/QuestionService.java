package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.question.QuestionRequestDto;
import com.UnderUpb.backendUnderUpb.dto.question.QuestionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    QuestionResponseDto createQuestion(QuestionRequestDto questionDto);
    QuestionResponseDto getQuestionById(UUID questionId);
    Page<QuestionResponseDto> getAllQuestions(Pageable pageable);
    QuestionResponseDto updateQuestion(UUID questionId, QuestionRequestDto questionDto);
    void deleteQuestion(UUID questionId);
    List<QuestionResponseDto> getQuestionsByLevel(Integer level);
    List<QuestionResponseDto> getQuestionsByLevelId(UUID levelId);
    List<QuestionResponseDto> getRandomQuestionsByLevel(Integer level, int count);
}
