package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.PurchaseService;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderRequestDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    @Override
    @Transactional
    public PurchaseOrderResponseDto createPurchaseOrder(PurchaseOrderRequestDto purchaseDto) {
        if (purchaseDto == null || purchaseDto.getUserId() == null) {
            throw new IllegalArgumentException("Purchase order data cannot be null");
        }
        PurchaseOrderResponseDto response = PurchaseOrderResponseDto.builder()
                .id(UUID.randomUUID())
                .userId(purchaseDto.getUserId())
                .itemName(purchaseDto.getItemName())
                .amount(purchaseDto.getAmount())
                .currency(purchaseDto.getCurrency())
                .description(purchaseDto.getDescription())
                .status("PENDING")
                .build();
        log.info("Purchase order created with ID: {}", response.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDto getPurchaseOrder(UUID orderId) {
        log.info("Retrieving purchase order with ID: {}", orderId);
        return PurchaseOrderResponseDto.builder()
                .id(orderId)
                .status("PENDING")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateWebhook(String signature, String payload) {
        return signature != null && !signature.isEmpty();
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDto completePurchase(UUID orderId) {
        log.info("Completing purchase order with ID: {}", orderId);
        return PurchaseOrderResponseDto.builder()
                .id(orderId)
                .status("COMPLETED")
                .build();
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDto failPurchase(UUID orderId) {
        log.info("Failing purchase order with ID: {}", orderId);
        return PurchaseOrderResponseDto.builder()
                .id(orderId)
                .status("FAILED")
                .build();
    }
}
