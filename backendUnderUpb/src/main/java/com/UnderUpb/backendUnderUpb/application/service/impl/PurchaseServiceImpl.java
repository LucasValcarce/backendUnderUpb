package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.PurchaseService;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderRequestDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Product;
import com.UnderUpb.backendUnderUpb.entity.Purchase;
import com.UnderUpb.backendUnderUpb.entity.PurchaseStatus;
import com.UnderUpb.backendUnderUpb.entity.User;
import com.UnderUpb.backendUnderUpb.repository.ProductRepository;
import com.UnderUpb.backendUnderUpb.repository.PurchaseRepository;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
import com.UnderUpb.backendUnderUpb.repository.OwnedProductRepository;
import com.UnderUpb.backendUnderUpb.entity.OwnedProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

        private final PurchaseRepository purchaseRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final OwnedProductRepository ownedProductRepository;

    @Override
    @Transactional
    public PurchaseOrderResponseDto createPurchaseOrder(PurchaseOrderRequestDto purchaseDto) {
        if (purchaseDto == null || purchaseDto.getUserId() == null) {
            throw new IllegalArgumentException("Purchase order data cannot be null");
        }

        User user = userRepository.findById(purchaseDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + purchaseDto.getUserId()));

        // Find or create product by name
        Product product = productRepository.findByName(purchaseDto.getItemName())
                .orElseGet(() -> {
                    Product p = Product.builder()
                            .name(purchaseDto.getItemName())
                            .description(purchaseDto.getDescription())
                            .price(purchaseDto.getAmount())
                            .currency(purchaseDto.getCurrency())
                            .build();
                    return productRepository.save(p);
                });

        Purchase purchase = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(1)
                .price(purchaseDto.getAmount())
                .currency(purchaseDto.getCurrency())
                .status(PurchaseStatus.PENDING)
                .build();

        Purchase saved = purchaseRepository.save(purchase);

        PurchaseOrderResponseDto response = PurchaseOrderResponseDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .itemName(product.getName())
                .amount(saved.getPrice())
                .currency(saved.getCurrency())
                .description(product.getDescription())
                .status(saved.getStatus().name())
                .build();

        log.info("Purchase order created with ID: {}", response.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDto getPurchaseOrder(UUID orderId) {
        Purchase p = purchaseRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found with ID: " + orderId));
        return PurchaseOrderResponseDto.builder()
                .id(p.getId())
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .itemName(p.getProduct() != null ? p.getProduct().getName() : null)
                .amount(p.getPrice())
                .currency(p.getCurrency())
                .description(p.getProduct() != null ? p.getProduct().getDescription() : null)
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .createdDate(p.getCreatedDate())
                .updatedDate(p.getUpdatedDate())
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
        Purchase p = purchaseRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found with ID: " + orderId));
        p.setStatus(PurchaseStatus.COMPLETED);
        Purchase saved = purchaseRepository.save(p);
                // Create an owned product record when a purchase is completed
                try {
                        OwnedProduct op = OwnedProduct.builder()
                                        .user(saved.getUser())
                                        .product(saved.getProduct())
                                        .purchase(saved)
                                        .isActive(Boolean.TRUE)
                                        .equipped(Boolean.FALSE)
                                        .build();
                        ownedProductRepository.save(op);
                } catch (Exception ex) {
                        log.warn("Failed to create owned product for purchase {}: {}", orderId, ex.getMessage());
                }
        log.info("Completing purchase order with ID: {}", orderId);
        return PurchaseOrderResponseDto.builder()
                .id(saved.getId())
                .status(saved.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public PurchaseOrderResponseDto failPurchase(UUID orderId) {
        Purchase p = purchaseRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found with ID: " + orderId));
        p.setStatus(PurchaseStatus.FAILED);
        Purchase saved = purchaseRepository.save(p);
        log.info("Failing purchase order with ID: {}", orderId);
        return PurchaseOrderResponseDto.builder()
                .id(saved.getId())
                .status(saved.getStatus().name())
                .build();
    }
}
