package com.UnderUpb.backendUnderUpb.implementations.upbolis.upbolis;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpbolisCreatePaymentResponseDto {
    private String externalPaymentId;
    private String paymentUrl;
}
