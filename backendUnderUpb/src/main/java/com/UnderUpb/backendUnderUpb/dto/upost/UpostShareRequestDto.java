package com.UnderUpb.backendUnderUpb.dto.upost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpostShareRequestDto {
    private String emailUsuario;
    private String password;
    private String mensaje;
}
