package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderRequestDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderResponseDto;

import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface PurchaseService {
    PurchaseOrderResponseDto createPurchaseOrder(PurchaseOrderRequestDto purchaseDto, String buyerToken);
    PurchaseOrderResponseDto getPurchaseOrder(UUID orderId);
    boolean validateWebhook(String signature, String payload);
    PurchaseOrderResponseDto completePurchase(UUID orderId);
    PurchaseOrderResponseDto failPurchase(UUID orderId);
    java.util.List<PurchaseOrderResponseDto> getPurchasesByUser(UUID userId);
    java.util.List<PurchaseOrderResponseDto> getPurchasesByProduct(UUID productId);
    Page<PurchaseOrderResponseDto> getAllPurchases(Pageable pageable);
    void deletePurchase(UUID orderId);
}
