package com.UnderUpb.backendUnderUpb.dto.upbolis;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpbolisRegisterRequestDto {
    private String name;
    private String email;
    private String password;
    private String password_confirmation;
}
