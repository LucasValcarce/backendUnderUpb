package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.PurchaseService;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderRequestDto;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchaseOrderResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Product;
import com.UnderUpb.backendUnderUpb.entity.Purchase;
import com.UnderUpb.backendUnderUpb.entity.PurchaseStatus;
import com.UnderUpb.backendUnderUpb.entity.User;
import com.UnderUpb.backendUnderUpb.implementations.upbolis.upbolis.UpbolisCreatePaymentRequestDto;
import com.UnderUpb.backendUnderUpb.repository.ProductRepository;
import com.UnderUpb.backendUnderUpb.repository.PurchaseRepository;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
import com.UnderUpb.backendUnderUpb.repository.OwnedProductRepository;
import com.UnderUpb.backendUnderUpb.entity.OwnedProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

        private final PurchaseRepository purchaseRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final OwnedProductRepository ownedProductRepository;
    private final com.UnderUpb.backendUnderUpb.implementations.upbolis.UpbolisService upbolisService;

    @Override
    @Transactional
    public PurchaseOrderResponseDto createPurchaseOrder(PurchaseOrderRequestDto purchaseDto) {
        if (purchaseDto == null || purchaseDto.getUserId() == null) {
            throw new IllegalArgumentException("Purchase order data cannot be null");
        }

        User user = userRepository.findById(purchaseDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + purchaseDto.getUserId()));

        // Ensure product exists (productId required)
        if (purchaseDto.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        Product product = productRepository.findById(purchaseDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + purchaseDto.getProductId()));

        Purchase purchase = Purchase.builder()
                .user(user)
                .product(product)
                .quantity(1)
                .price(purchaseDto.getAmount())
                .currency(purchaseDto.getCurrency())
                .status(PurchaseStatus.PENDING)
                .build();

        Purchase saved = purchaseRepository.save(purchase);

        // Create payment on Upbolis (external gateway) using DTO to avoid passing entity
        try {
            var req = UpbolisCreatePaymentRequestDto.builder()
                    .orderId(saved.getId())
                    .amount(BigDecimal.valueOf(saved.getPrice()))
                    .currency(saved.getCurrency())
                    .description(saved.getProduct() != null ? saved.getProduct().getName() : null)
                    .buyerId(saved.getUser() != null ? saved.getUser().getId() : null)
                    .webhookUrl(null)
                    .build();
            var resp = upbolisService.createPayment(req);
            String externalId = resp.getExternalPaymentId();
            String paymentUrl = resp.getPaymentUrl();
            if (externalId != null) saved.setExternalPaymentId(externalId);
            if (paymentUrl != null) saved.setPaymentUrl(paymentUrl);
            purchaseRepository.save(saved);
        } catch (Exception ex) {
            log.warn("Failed to create payment on Upbolis for purchase {}: {}", saved.getId(), ex.getMessage());
        }

        PurchaseOrderResponseDto response = PurchaseOrderResponseDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .productId(product.getId())
                .itemName(product.getName())
                .amount(saved.getPrice())
                .currency(saved.getCurrency())
                .description(product.getDescription())
                .status(saved.getStatus().name())
                .externalPaymentId(saved.getExternalPaymentId())
                .paymentUrl(saved.getPaymentUrl())
                .build();

        log.info("Purchase order created with ID: {}", response.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDto getPurchaseOrder(UUID orderId) {
        Purchase p = purchaseRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase not found with ID: " + orderId));
        // If external payment exists and local status is PENDING, attempt to refresh status from Upbolis
        if (p.getExternalPaymentId() != null && p.getStatus() == PurchaseStatus.PENDING) {
            try {
                String status = upbolisService.getPaymentStatus(p.getExternalPaymentId());
                if (status != null) {
                    if (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("succeeded") || status.equalsIgnoreCase("paid")) {
                        p.setStatus(PurchaseStatus.COMPLETED);
                        purchaseRepository.save(p);
                    } else if (status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("cancelled") || status.equalsIgnoreCase("declined")) {
                        p.setStatus(PurchaseStatus.FAILED);
                        purchaseRepository.save(p);
                    }
                }
            } catch (Exception ex) {
                log.debug("Could not refresh payment status from Upbolis: {}", ex.getMessage());
            }
        }

        return PurchaseOrderResponseDto.builder()
                .id(p.getId())
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .productId(p.getProduct() != null ? p.getProduct().getId() : null)
                .itemName(p.getProduct() != null ? p.getProduct().getName() : null)
                .amount(p.getPrice())
                .currency(p.getCurrency())
                .description(p.getProduct() != null ? p.getProduct().getDescription() : null)
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .externalPaymentId(p.getExternalPaymentId())
                .paymentUrl(p.getPaymentUrl())
                .createdDate(p.getCreatedDate())
                .updatedDate(p.getUpdatedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<PurchaseOrderResponseDto> getPurchasesByUser(UUID userId) {
        return purchaseRepository.findByUserId(userId).stream()
                .map(p -> PurchaseOrderResponseDto.builder()
                        .id(p.getId())
                        .userId(p.getUser() != null ? p.getUser().getId() : null)
                        .productId(p.getProduct() != null ? p.getProduct().getId() : null)
                        .itemName(p.getProduct() != null ? p.getProduct().getName() : null)
                        .amount(p.getPrice())
                        .currency(p.getCurrency())
                        .description(p.getProduct() != null ? p.getProduct().getDescription() : null)
                        .status(p.getStatus() != null ? p.getStatus().name() : null)
                        .externalPaymentId(p.getExternalPaymentId())
                        .paymentUrl(p.getPaymentUrl())
                        .createdDate(p.getCreatedDate())
                        .updatedDate(p.getUpdatedDate())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<PurchaseOrderResponseDto> getPurchasesByProduct(UUID productId) {
        return purchaseRepository.findByProductId(productId).stream()
                .map(p -> PurchaseOrderResponseDto.builder()
                        .id(p.getId())
                        .userId(p.getUser() != null ? p.getUser().getId() : null)
                        .productId(p.getProduct() != null ? p.getProduct().getId() : null)
                        .itemName(p.getProduct() != null ? p.getProduct().getName() : null)
                        .amount(p.getPrice())
                        .currency(p.getCurrency())
                        .description(p.getProduct() != null ? p.getProduct().getDescription() : null)
                        .status(p.getStatus() != null ? p.getStatus().name() : null)
                        .externalPaymentId(p.getExternalPaymentId())
                        .paymentUrl(p.getPaymentUrl())
                        .createdDate(p.getCreatedDate())
                        .updatedDate(p.getUpdatedDate())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<PurchaseOrderResponseDto> getAllPurchases(org.springframework.data.domain.Pageable pageable) {
        return purchaseRepository.findAll(pageable)
                .map(p -> PurchaseOrderResponseDto.builder()
                        .id(p.getId())
                        .userId(p.getUser() != null ? p.getUser().getId() : null)
                        .productId(p.getProduct() != null ? p.getProduct().getId() : null)
                        .itemName(p.getProduct() != null ? p.getProduct().getName() : null)
                        .amount(p.getPrice())
                        .currency(p.getCurrency())
                        .description(p.getProduct() != null ? p.getProduct().getDescription() : null)
                        .status(p.getStatus() != null ? p.getStatus().name() : null)
                        .externalPaymentId(p.getExternalPaymentId())
                        .paymentUrl(p.getPaymentUrl())
                        .createdDate(p.getCreatedDate())
                        .updatedDate(p.getUpdatedDate())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateWebhook(String signature, String payload) {
        boolean ok = upbolisService.verifyWebhook(signature, payload);
        if (!ok) return false;

        try {
            var data = upbolisService.parseWebhook(payload);
            String externalId = data.get("externalPaymentId");
            String status = data.get("status");
            if (externalId != null && status != null) {
                Purchase p = purchaseRepository.findByExternalPaymentId(externalId);
                if (p != null) {
                    if (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("succeeded") || status.equalsIgnoreCase("paid")) {
                        completePurchase(p.getId());
                    } else if (status.equalsIgnoreCase("failed") || status.equalsIgnoreCase("cancelled") || status.equalsIgnoreCase("declined")) {
                        failPurchase(p.getId());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to parse webhook payload: {}", ex.getMessage());
        }
        return true;
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

    @Override
    @Transactional
    public void deletePurchase(UUID orderId) {
        if (!purchaseRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Purchase not found with ID: " + orderId);
        }
        purchaseRepository.deleteById(orderId);
        log.info("Deleted purchase order with ID: {}", orderId);
    }
}
