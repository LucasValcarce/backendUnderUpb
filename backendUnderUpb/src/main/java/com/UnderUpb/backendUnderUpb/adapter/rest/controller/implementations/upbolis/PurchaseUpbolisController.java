package com.UnderUpb.backendUnderUpb.adapter.rest.controller.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseWithUpbolisTokenDto;
import com.UnderUpb.backendUnderUpb.entity.Purchase;
import com.UnderUpb.backendUnderUpb.implementations.upbolis.UpbilisPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Upbolis - Purchases", description = "Endpoints for purchases and purchase verification using Upbolis")
public class PurchaseUpbolisController {

    private final UpbilisPurchaseService upbilisPurchaseService;

    /**
     * Crea una compra verificada con token de Upbolis
     * POST /api/purchases/upbolis
     * 
     * El usuario debe proporcionar su token de Upbolis para realizar la compra.
     * El sistema verificará que el token sea válido antes de crear la compra.
     */
    @PostMapping("/upbolis")
    public ResponseEntity<?> createPurchaseWithUpbolis(
            @RequestBody PurchaseWithUpbolisTokenDto purchaseDto) {
        try {
            if (purchaseDto.getUserId() == null || purchaseDto.getProductId() == null) {
                return ResponseEntity.badRequest()
                        .body("User ID and Product ID are required");
            }

            log.info("Creating purchase with Upbolis verification for user: {}, product: {}",
                    purchaseDto.getUserId(), purchaseDto.getProductId());

            Purchase purchase = upbilisPurchaseService.createPurchaseWithUpbolisVerification(
                    purchaseDto.getUserId(),
                    purchaseDto.getProductId(),
                    purchaseDto.getQuantity()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(purchase);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User or Product not found: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Failed to verify Upbolis token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating purchase with Upbolis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating purchase: " + e.getMessage());
        }
    }

    /**
     * Verifica si un usuario puede realizar compras (tiene token válido)
     * GET /api/purchases/upbolis/can-purchase/{userId}
     */
    @GetMapping("/upbolis/can-purchase/{userId}")
    public ResponseEntity<?> canPurchaseWithUpbolis(@PathVariable UUID userId) {
        try {
            boolean canPurchase = upbilisPurchaseService.validateUserUpbolisToken(userId);

            return ResponseEntity.ok()
                    .body("{ \"can_purchase\": " + canPurchase + " }");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    /**
     * Obtiene el token de Upbolis de un usuario (para verificar que está autenticado)
     * GET /api/purchases/upbolis/token/{userId}
     * 
     * NOTA: Usar con cuidado - no exposer este endpoint públicamente
     */
    @GetMapping("/upbolis/token/{userId}")
    public ResponseEntity<?> getUserUpbolisToken(@PathVariable UUID userId) {
        try {
            String token = upbilisPurchaseService.getUserUpbolisToken(userId);
            // Retornar solo la información de que tiene token, no el token en sí
            return ResponseEntity.ok()
                    .body("{ \"has_token\": true, \"token_length\": " + token.length() + " }");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User has not authenticated with Upbolis");
        }
    }
}
