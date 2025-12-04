package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.answer.AnswerRequestDto;
import com.UnderUpb.backendUnderUpb.dto.answer.AnswerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AnswerService {
    AnswerResponseDto createAnswer(AnswerRequestDto answerDto);
    AnswerResponseDto getAnswerById(UUID answerId);
    Page<AnswerResponseDto> getAllAnswers(Pageable pageable);
    AnswerResponseDto updateAnswer(UUID answerId, AnswerRequestDto answerDto);
    void deleteAnswer(UUID answerId);
    List<AnswerResponseDto> getAnswersByQuestion(UUID questionId);
    List<AnswerResponseDto> getCorrectAnswersByQuestion(UUID questionId);
}
