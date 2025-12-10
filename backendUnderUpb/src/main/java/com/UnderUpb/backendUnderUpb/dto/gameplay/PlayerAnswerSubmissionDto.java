package com.UnderUpb.backendUnderUpb.dto.gameplay;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PlayerAnswerSubmissionDto {
    private UUID userId;
    private UUID questionId;
    private UUID answerId;
    private Boolean isCorrect;
}
