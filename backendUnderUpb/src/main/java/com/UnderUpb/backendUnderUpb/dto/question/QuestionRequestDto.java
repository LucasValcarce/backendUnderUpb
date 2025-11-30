package com.UnderUpb.backendUnderUpb.dto.question;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuestionRequestDto {
    private String text;
    private Integer level;
    private String optionsJson;
    private String answer;
    private String description;
}
