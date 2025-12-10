package com.UnderUpb.backendUnderUpb.dto.gameplay;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LevelCompletionResponseDto {
    private UUID userId;
    private Integer currentLevel;
    private Integer totalScore;
    private Integer pointsEarned;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Boolean levelPassed;
    private String message;
}
