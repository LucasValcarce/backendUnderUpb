package com.UnderUpb.backendUnderUpb.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.entity.Product;
import com.UnderUpb.backendUnderUpb.event.ProductChangeEvent;
import com.UnderUpb.backendUnderUpb.event.ProductChangeEvent.Action;
import com.UnderUpb.backendUnderUpb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

/**
 * Escucha eventos de productos para sincronizarlos automáticamente con Upbolis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSyncEventListener {

    private final UpbolisProductSyncService upbolisProductSyncService;
    private final ProductRepository productRepository;

    /**
     * Se ejecuta cuando un producto es creado, actualizado o eliminado
     * Automáticamente sincroniza con Upbolis según la acción
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductChanged(ProductChangeEvent event) {
        try {
            Product product = event.getProduct();
            Action action = event.getAction();

            if (action == Action.CREATED) {
                log.info("New product detected: {}. Syncing to Upbolis...", product.getName());
                upbolisProductSyncService.syncProductToUpbolis(product);

                // Verify persisted state after sync
                try {
                    productRepository.findById(product.getId()).ifPresent(p -> log.info("Post-sync DB product: {}", p.toString()));
                } catch (Exception e) {
                    log.debug("Could not verify product after sync: {}", e.getMessage());
                }
            } else if (action == Action.UPDATED) {
                log.info("Product updated: {}. Syncing to Upbolis...", product.getName());
                upbolisProductSyncService.syncProductToUpbolis(product);

                try {
                    productRepository.findById(product.getId()).ifPresent(p -> log.info("Post-sync DB product: {}", p.toString()));
                } catch (Exception e) {
                    log.debug("Could not verify product after sync: {}", e.getMessage());
                }
            } else if (action == Action.DELETED) {
                log.info("Product deleted: {}. Removing from Upbolis...", product.getName());
                upbolisProductSyncService.removeProductFromUpbolis(product);
            }
        } catch (Exception e) {
            log.error("Error syncing product to Upbolis", e);
            // No lanzar excepción para no afectar la transacción principal
        }
    }
}
