package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.application.service.PurchaseService;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderRequestDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchase Management", description = "Endpoints for managing in-game purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final com.UnderUpb.backendUnderUpb.implementations.upbolis.UpbilisPurchaseService upbilisPurchaseService;

    @PostMapping("/orders")
    @Operation(summary = "Create purchase order", description = "Creates a new purchase order")
    @ApiResponse(responseCode = "201", description = "Purchase order created successfully")
    public ResponseEntity<?> createPurchaseOrder(
            @RequestBody PurchaseOrderRequestDto purchaseDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            jakarta.servlet.http.HttpServletRequest servletRequest) {
        // Allow tokens passed in several possible header names or as raw value
        String token = null;
        if (authHeader != null && !authHeader.isBlank()) {
            token = authHeader.startsWith("Bearer ") ? authHeader.substring("Bearer ".length()).trim() : authHeader.trim();
        }
        if ((token == null || token.isBlank())) {
            // Fallback to common alternative headers
            String alt = servletRequest.getHeader("Auth");
            if (alt == null) alt = servletRequest.getHeader("X-Auth-Token");
            if (alt == null) alt = servletRequest.getHeader("X-Authorization");
            if (alt != null && !alt.isBlank()) token = alt.startsWith("Bearer ") ? alt.substring("Bearer ".length()).trim() : alt.trim();
        }
        if (token == null || token.isBlank()) {
            log.debug("Missing buyer token in headers; attempting to fetch from user's stored token. headers Authorization='{}' Auth='{}' X-Auth-Token='{}' X-Authorization='{}'",
                    authHeader, servletRequest.getHeader("Auth"), servletRequest.getHeader("X-Auth-Token"), servletRequest.getHeader("X-Authorization"));

            // If no header token provided, try to use token saved on the user (if userId present)
            if (purchaseDto != null && purchaseDto.getUserId() != null) {
                try {
                    String userToken = upbilisPurchaseService.getUserUpbolisToken(purchaseDto.getUserId());
                    if (userToken != null && !userToken.isBlank()) {
                        token = userToken;
                    }
                } catch (Exception e) {
                    log.debug("Could not obtain Upbolis token from user {}: {}", purchaseDto.getUserId(), e.getMessage());
                }
            }

            if (token == null || token.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "Buyer authorization missing. Please login to UPBolis and pass the token in Authorization header (Bearer <token>) or use 'Auth'/'X-Auth-Token' header."));
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.createPurchaseOrder(purchaseDto, token));
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
