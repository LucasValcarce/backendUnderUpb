package com.UnderUpb.backendUnderUpb.dto.answer;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AnswerRequestDto {
    private UUID questionId;
    private String text;
    private Boolean isCorrect;
    private String explanation;
}
