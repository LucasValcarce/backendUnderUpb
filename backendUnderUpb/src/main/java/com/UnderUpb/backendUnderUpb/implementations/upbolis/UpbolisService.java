package com.UnderUpb.backendUnderUpb.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.implementations.upbolis.upbolis.UpbolisCreatePaymentRequestDto;
import com.UnderUpb.backendUnderUpb.implementations.upbolis.upbolis.UpbolisCreatePaymentResponseDto;

import java.util.Map;

public interface UpbolisService {
    /**
     * Create an external payment on UPBolis and return the created payment info
     */
    UpbolisCreatePaymentResponseDto createPayment(UpbolisCreatePaymentRequestDto request) throws Exception;

    /**
     * Verify webhook signature header is valid for given payload
     */
    boolean verifyWebhook(String signatureHeader, String payload);

    /**
     * Parse webhook payload into a map (externalPaymentId, status)
     */
    Map<String, String> parseWebhook(String payload) throws Exception;

    /**
     * Query payment status by external id
     */
    String getPaymentStatus(String externalPaymentId) throws Exception;
}

