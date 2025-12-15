package com.UnderUpb.backendUnderUpb.dto.upbolis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpbolisLoginResponseDto {
    private String token;
    
    @JsonProperty("user_id")
    private Long userId;
    
    private String message;
    private Boolean success;
}
