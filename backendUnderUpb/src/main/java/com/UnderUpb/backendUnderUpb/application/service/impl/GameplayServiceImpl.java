package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.GameplayService;
import com.UnderUpb.backendUnderUpb.dto.gameplay.LevelCompletionDto;
import com.UnderUpb.backendUnderUpb.dto.gameplay.LevelCompletionResponseDto;
import com.UnderUpb.backendUnderUpb.entity.User;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameplayServiceImpl implements GameplayService {

    private final UserRepository userRepository;

    private static final int POINTS_PER_CORRECT_ANSWER = 100;
    private static final int POINTS_PENALTY_PER_WRONG_ANSWER = 20;
    private static final int MIN_PASS_PERCENTAGE = 60;

    @Override
    @Transactional
    public LevelCompletionResponseDto completeLevel(LevelCompletionDto completionDto) {
        User user = userRepository.findById(completionDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + completionDto.getUserId()));

        int correctAnswers = completionDto.getCorrectAnswers();
        int totalQuestions = completionDto.getTotalQuestions();
        
        // Calcular porcentaje de acierto
        double percentage = (correctAnswers * 100.0) / totalQuestions;
        boolean levelPassed = percentage >= MIN_PASS_PERCENTAGE;

        // Calcular puntos
        int correctPoints = correctAnswers * POINTS_PER_CORRECT_ANSWER;
        int wrongAnswers = totalQuestions - correctAnswers;
        int penaltyPoints = wrongAnswers * POINTS_PENALTY_PER_WRONG_ANSWER;
        int pointsEarned = Math.max(0, correctPoints - penaltyPoints);

        // Actualizar score del usuario
        user.setScore(user.getScore() + pointsEarned);

        // Actualizar vida (aumentar en respuestas correctas, disminuir en incorrectas)
        int lifeChange = (correctAnswers * 10) - (wrongAnswers * 5);
        int newLifePoints = Math.min(user.getMaxLifePoints(), user.getLifePoints() + lifeChange);
        newLifePoints = Math.max(0, newLifePoints);
        user.setLifePoints(newLifePoints);

        // Avanzar al siguiente nivel si pasó
        if (levelPassed && completionDto.getLevelNumber() == user.getCurrentLevel()) {
            user.setCurrentLevel(user.getCurrentLevel() + 1);
            log.info("User {} advanced to level {}", user.getId(), user.getCurrentLevel());
        }

        User updatedUser = userRepository.save(user);

        String message = levelPassed ? 
            String.format("¡Felicidades! Completaste el nivel %d. Porcentaje: %.1f%%", 
                completionDto.getLevelNumber(), percentage) :
            String.format("No pasaste el nivel %d. Necesitas %.1f%% para pasar. Porcentaje: %.1f%%", 
                completionDto.getLevelNumber(), (double)MIN_PASS_PERCENTAGE, percentage);

        return LevelCompletionResponseDto.builder()
                .userId(updatedUser.getId())
                .currentLevel(updatedUser.getCurrentLevel())
                .totalScore(updatedUser.getScore())
                .pointsEarned(pointsEarned)
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .levelPassed(levelPassed)
                .message(message)
                .build();
    }
}
