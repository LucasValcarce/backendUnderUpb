package com.UnderUpb.backendUnderUpb.implementations.upbolis.upbolis;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UpbolisCreatePaymentRequestDto {
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private UUID buyerId;
    private String webhookUrl;
}
