package com.UnderUpb.backendUnderUpb.dto.upbolis;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpbolisLoginRequestDto {
    private String email;
    private String password;
}
