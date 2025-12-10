package com.UnderUpb.backendUnderUpb.dto.gameplay;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LevelCompletionDto {
    private UUID userId;
    private Integer levelNumber;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer pointsEarned;
    private List<PlayerAnswerSubmissionDto> answers;
}
