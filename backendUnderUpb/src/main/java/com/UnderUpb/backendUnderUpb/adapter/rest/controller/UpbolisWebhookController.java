package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisWebhookPurchaseDto;
import com.UnderUpb.backendUnderUpb.entity.Purchase;
import com.UnderUpb.backendUnderUpb.implementations.upbolis.UpbilisPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Upbolis - Webhooks", description = "Endpoints to receive and test webhooks from Upbolis")
public class UpbolisWebhookController {

    private final UpbilisPurchaseService upbilisPurchaseService;

    /**
     * Endpoint para recibir webhooks de compras desde Upbolis
     */
    @PostMapping("/upbolis/purchase")
    public ResponseEntity<?> receivePurchaseWebhook(@RequestBody UpbolisWebhookPurchaseDto webhookDto) {
        try {
            log.info("Received purchase webhook from Upbolis: buyer={}, product={}, amount={}", 
                    webhookDto.getBuyerUsername(), webhookDto.getProduct(), webhookDto.getAmount());

            if (webhookDto.getBuyerUsername() == null || webhookDto.getProduct() == null) {
                return ResponseEntity.badRequest()
                        .body("Missing required fields: buyer_username or product");
            }

                Purchase purchase = upbilisPurchaseService.processPurchaseWebhook(
                    webhookDto.getBuyerUsername(),
                    webhookDto.getProduct(),
                    webhookDto.getAmount(),
                    webhookDto.getProductId() != null ? webhookDto.getProductId() : 0L,
                    webhookDto.getTransactionId()
                );

            return ResponseEntity.ok()
                    .body("Purchase processed successfully. Purchase ID: " + purchase.getId());
        } catch (Exception e) {
            log.error("Error processing purchase webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }

    /**
     * Endpoint de prueba para verificar que el webhook está funcionando
     */
    @GetMapping("/upbolis/health")
    public ResponseEntity<?> webhookHealth() {
        return ResponseEntity.ok("Upbolis webhook service is running");
    }
}
