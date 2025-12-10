package com.UnderUpb.backendUnderUpb.dto.question;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuestionResponseDto {
    private UUID id;
    private String text;
    private UUID levelId;
    private String description;
    private List<AnswerDto> answers;
    private Instant createdDate;
    private Instant updatedDate;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class AnswerDto {
        private UUID id;
        private String text;
        private Boolean isCorrect;
        private String explanation;
    }
}
