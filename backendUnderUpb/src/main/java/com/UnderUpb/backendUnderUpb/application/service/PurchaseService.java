package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderRequestDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderResponseDto;

import java.util.UUID;

public interface PurchaseService {
    PurchaseOrderResponseDto createPurchaseOrder(PurchaseOrderRequestDto purchaseDto);
    PurchaseOrderResponseDto getPurchaseOrder(UUID orderId);
    boolean validateWebhook(String signature, String payload);
    PurchaseOrderResponseDto completePurchase(UUID orderId);
    PurchaseOrderResponseDto failPurchase(UUID orderId);
}
