package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.QuestionService;
import com.UnderUpb.backendUnderUpb.dto.question.QuestionRequestDto;
import com.UnderUpb.backendUnderUpb.dto.question.QuestionResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Question;
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
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public QuestionResponseDto createQuestion(QuestionRequestDto questionDto) {
        if (questionDto == null || questionDto.getText() == null) {
            throw new IllegalArgumentException("Question data cannot be null");
        }
        Question question = Question.builder()
                .text(questionDto.getText())
                .level(questionDto.getLevel())
                .description(questionDto.getDescription())
                .build();
        Question savedQuestion = questionRepository.save(question);
        log.info("Question created with ID: {}", savedQuestion.getId());
        return toResponseDto(savedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionById(UUID questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));
        return toResponseDto(question);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public QuestionResponseDto updateQuestion(UUID questionId, QuestionRequestDto questionDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));
        if (questionDto.getText() != null) {
            question.setText(questionDto.getText());
        }
        if (questionDto.getLevel() != null) {
            question.setLevel(questionDto.getLevel());
        }
        if (questionDto.getDescription() != null) {
            question.setDescription(questionDto.getDescription());
        }
        Question updatedQuestion = questionRepository.save(question);
        log.info("Question updated with ID: {}", questionId);
        return toResponseDto(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalArgumentException("Question not found with ID: " + questionId);
        }
        questionRepository.deleteById(questionId);
        log.info("Question deleted with ID: {}", questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByLevel(Integer level) {
        return questionRepository.findByLevel(level).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getRandomQuestionsByLevel(Integer level, int count) {
        List<Question> questions = questionRepository.findByLevel(level);
        if (questions.isEmpty()) {
            return List.of();
        }
        java.util.Collections.shuffle(questions);
        if (count <= 0 || count >= questions.size()) {
            return questions.stream().map(this::toResponseDto).toList();
        }
        return questions.stream()
                .limit(count)
                .map(this::toResponseDto)
                .toList();
    }

    private QuestionResponseDto toResponseDto(Question question) {
        return QuestionResponseDto.builder()
                .id(question.getId())
                .text(question.getText())
                .level(question.getLevel())
                .description(question.getDescription())
                .createdDate(question.getCreatedDate())
                .updatedDate(question.getUpdatedDate())
                .build();
    }
}
