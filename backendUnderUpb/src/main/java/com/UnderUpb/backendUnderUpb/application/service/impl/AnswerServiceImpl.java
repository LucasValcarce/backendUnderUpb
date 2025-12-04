package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.AnswerService;
import com.UnderUpb.backendUnderUpb.dto.answer.AnswerRequestDto;
import com.UnderUpb.backendUnderUpb.dto.answer.AnswerResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Answers;
import com.UnderUpb.backendUnderUpb.entity.Question;
import com.UnderUpb.backendUnderUpb.repository.AnswerRepository;
import com.UnderUpb.backendUnderUpb.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public AnswerResponseDto createAnswer(AnswerRequestDto answerDto) {
        if (answerDto == null || answerDto.getQuestionId() == null || answerDto.getText() == null) {
            throw new IllegalArgumentException("Answer data cannot be null");
        }
        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + answerDto.getQuestionId()));
        
        Answers answer = Answers.builder()
                .question(question)
                .text(answerDto.getText())
                .isCorrect(answerDto.getIsCorrect() != null ? answerDto.getIsCorrect() : false)
                .explanation(answerDto.getExplanation())
                .build();
        Answers savedAnswer = answerRepository.save(answer);
        log.info("Answer created with ID: {}", savedAnswer.getId());
        return toResponseDto(savedAnswer);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerResponseDto getAnswerById(UUID answerId) {
        Answers answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerId));
        return toResponseDto(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnswerResponseDto> getAllAnswers(Pageable pageable) {
        return answerRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public AnswerResponseDto updateAnswer(UUID answerId, AnswerRequestDto answerDto) {
        Answers answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerId));
        
        if (answerDto.getText() != null) {
            answer.setText(answerDto.getText());
        }
        if (answerDto.getIsCorrect() != null) {
            answer.setIsCorrect(answerDto.getIsCorrect());
        }
        if (answerDto.getExplanation() != null) {
            answer.setExplanation(answerDto.getExplanation());
        }
        if (answerDto.getQuestionId() != null && !answerDto.getQuestionId().equals(answer.getQuestion().getId())) {
            Question newQuestion = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + answerDto.getQuestionId()));
            answer.setQuestion(newQuestion);
        }
        Answers updatedAnswer = answerRepository.save(answer);
        log.info("Answer updated with ID: {}", answerId);
        return toResponseDto(updatedAnswer);
    }

    @Override
    @Transactional
    public void deleteAnswer(UUID answerId) {
        if (!answerRepository.existsById(answerId)) {
            throw new IllegalArgumentException("Answer not found with ID: " + answerId);
        }
        answerRepository.deleteById(answerId);
        log.info("Answer deleted with ID: {}", answerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getAnswersByQuestion(UUID questionId) {
        return answerRepository.findByQuestionId(questionId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getCorrectAnswersByQuestion(UUID questionId) {
        return answerRepository.findByQuestionIdAndIsCorrect(questionId, true).stream()
                .map(this::toResponseDto)
                .toList();
    }

    private AnswerResponseDto toResponseDto(Answers answer) {
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .questionId(answer.getQuestion().getId())
                .text(answer.getText())
                .isCorrect(answer.getIsCorrect())
                .explanation(answer.getExplanation())
                .createdDate(answer.getCreatedDate())
                .updatedDate(answer.getUpdatedDate())
                .build();
    }
}
