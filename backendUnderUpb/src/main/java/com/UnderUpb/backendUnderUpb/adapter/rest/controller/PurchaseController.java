package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.PurchaseService;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderRequestDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchase Management", description = "Endpoints for managing in-game purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/orders")
    @Operation(summary = "Create purchase order", description = "Creates a new purchase order")
    @ApiResponse(responseCode = "201", description = "Purchase order created successfully")
    public ResponseEntity<PurchaseOrderResponseDto> createPurchaseOrder(
            @RequestBody PurchaseOrderRequestDto purchaseDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.createPurchaseOrder(purchaseDto));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get purchase order", description = "Retrieves a purchase order by its ID")
    @ApiResponse(responseCode = "200", description = "Purchase order retrieved successfully")
    public ResponseEntity<PurchaseOrderResponseDto> getPurchaseOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(purchaseService.getPurchaseOrder(orderId));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get purchases by user", description = "Retrieves purchases for a given user")
    @ApiResponse(responseCode = "200", description = "Purchases retrieved successfully")
    public ResponseEntity<java.util.List<PurchaseOrderResponseDto>> getPurchasesByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(purchaseService.getPurchasesByUser(userId));
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Get purchases by product", description = "Retrieves purchases for a given product")
    @ApiResponse(responseCode = "200", description = "Purchases retrieved successfully")
    public ResponseEntity<java.util.List<PurchaseOrderResponseDto>> getPurchasesByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(purchaseService.getPurchasesByProduct(productId));
    }

    @GetMapping
    @Operation(summary = "Get all purchases", description = "Retrieves all purchases with pagination")
    @ApiResponse(responseCode = "200", description = "Purchases retrieved successfully")
    public ResponseEntity<org.springframework.data.domain.Page<PurchaseOrderResponseDto>> getAllPurchases(org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(purchaseService.getAllPurchases(pageable));
    }

    @PostMapping("/webhook/payment-callback")
    @Operation(summary = "Payment callback", description = "Handles payment provider webhooks")
    @ApiResponse(responseCode = "200", description = "Webhook processed successfully")
    public ResponseEntity<Boolean> paymentCallback(
            @RequestHeader("X-Signature") String signature,
            @RequestBody String payload) {
        return ResponseEntity.ok(purchaseService.validateWebhook(signature, payload));
    }

    @PutMapping("/orders/{orderId}/complete")
    @Operation(summary = "Complete purchase", description = "Marks a purchase order as completed")
    @ApiResponse(responseCode = "200", description = "Purchase completed successfully")
    public ResponseEntity<PurchaseOrderResponseDto> completePurchase(@PathVariable UUID orderId) {
        return ResponseEntity.ok(purchaseService.completePurchase(orderId));
    }

    @PutMapping("/orders/{orderId}/fail")
    @Operation(summary = "Fail purchase", description = "Marks a purchase order as failed")
    @ApiResponse(responseCode = "200", description = "Purchase marked as failed")
    public ResponseEntity<PurchaseOrderResponseDto> failPurchase(@PathVariable UUID orderId) {
        return ResponseEntity.ok(purchaseService.failPurchase(orderId));
    }

    @DeleteMapping("/orders/{orderId}")
    @Operation(summary = "Delete purchase", description = "Deletes a purchase order")
    @ApiResponse(responseCode = "204", description = "Purchase deleted successfully")
    public ResponseEntity<Void> deletePurchase(@PathVariable UUID orderId) {
        purchaseService.deletePurchase(orderId);
        return ResponseEntity.noContent().build();
    }
}
