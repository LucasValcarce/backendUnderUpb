package com.UnderUpb.backendUnderUpb.dto.upbolis;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpbolisUserLoginDto {
    private String email;
    private String password;
    private String upbolisToken;
}
