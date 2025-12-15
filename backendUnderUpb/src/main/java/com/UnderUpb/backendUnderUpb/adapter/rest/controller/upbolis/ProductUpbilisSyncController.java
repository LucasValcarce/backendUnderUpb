package com.UnderUpb.backendUnderUpb.adapter.rest.controller.upbolis;

import com.UnderUpb.backendUnderUpb.entity.Product;
import com.UnderUpb.backendUnderUpb.implementations.upbolis.UpbolisProductSyncService;
import com.UnderUpb.backendUnderUpb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Upbolis - Products", description = "Endpoints to sync products between local system and Upbolis")
public class ProductUpbilisSyncController {

    private final UpbolisProductSyncService upbolisProductSyncService;
    private final ProductRepository productRepository;

    /**
     * Sincroniza un producto específico con Upbolis
     * POST /api/products/{productId}/sync-upbolis
     */
    @PostMapping("/{productId}/sync-upbolis")
    public ResponseEntity<?> syncProductToUpbolis(@PathVariable UUID productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            Product syncedProduct = upbolisProductSyncService.syncProductToUpbolis(product);

            return ResponseEntity.ok()
                    .body(syncedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        } catch (Exception e) {
            log.error("Error syncing product to Upbolis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error syncing product: " + e.getMessage());
        }
    }

    /**
     * Sincroniza todos los productos desde Upbolis
     * POST /api/products/sync-all
     */
    @PostMapping("/sync-all")
    public ResponseEntity<?> syncAllProductsFromUpbolis() {
        try {
            upbolisProductSyncService.syncAllProductsToUpbolis();

            return ResponseEntity.ok()
                    .body("All products synced from Upbolis successfully");
        } catch (Exception e) {
            log.error("Error syncing all products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error syncing products: " + e.getMessage());
        }
    }

    /**
     * Obtiene el estado de sincronización de un producto
     * GET /api/products/{productId}/upbolis-status
     */
    @GetMapping("/{productId}/upbolis-status")
    public ResponseEntity<?> getProductUpbolisStatus(@PathVariable UUID productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            boolean isSynced = product.getUpbolisProductId() != null && !product.getUpbolisProductId().isEmpty();

            return ResponseEntity.ok()
                    .body("{ \"synced\": " + isSynced + ", \"upbolis_id\": \"" + 
                            (product.getUpbolisProductId() != null ? product.getUpbolisProductId() : "null") + "\" }");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }
    }
}
