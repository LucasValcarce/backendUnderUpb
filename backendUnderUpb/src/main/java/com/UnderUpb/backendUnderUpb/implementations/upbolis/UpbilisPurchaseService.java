package com.UnderUpb.backendUnderUpb.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisLoginResponseDto;
import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisProductDto;
import com.UnderUpb.backendUnderUpb.entity.Product;
import com.UnderUpb.backendUnderUpb.entity.Purchase;
import com.UnderUpb.backendUnderUpb.entity.PurchaseStatus;
import com.UnderUpb.backendUnderUpb.entity.User;
import com.UnderUpb.backendUnderUpb.repository.OwnedProductRepository;
import com.UnderUpb.backendUnderUpb.repository.ProductRepository;
import com.UnderUpb.backendUnderUpb.repository.PurchaseRepository;
import com.UnderUpb.backendUnderUpb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpbilisPurchaseService {

    private final UpbolisApiClient upbolisApiClient;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OwnedProductRepository ownedProductRepository;

    /**
     * Autentica un usuario con sus credenciales en Upbolis y guarda el token
     */
    @Transactional
    public UpbolisLoginResponseDto authenticateUserWithUpbolis(UUID userId, String email, String password) {
        try {
            UpbolisLoginResponseDto loginResponse;
            try {
                loginResponse = upbolisApiClient.loginUser(email, password);
            } catch (RuntimeException ex) {
                // If authentication failed due to unauthorized, try to register the user on Upbolis and retry
                if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unauthorized")) {
                    log.info("User {} not found on Upbolis or invalid credentials; attempting to register on Upbolis", userId);
                    // attempt registration using local user name if available
                    User local = userRepository.findById(userId).orElse(null);
                    String name = local != null && local.getName() != null ? local.getName() : email.split("@")[0];
                    try {
                        UpbolisLoginResponseDto regResponse = upbolisApiClient.registerUser(name, email, password);
                        if (regResponse != null && regResponse.getToken() != null) {
                            loginResponse = regResponse;
                        } else {
                            // if register didn't return token, try to login again
                            loginResponse = upbolisApiClient.loginUser(email, password);
                        }
                    } catch (Exception rex) {
                        log.error("Registration attempt failed: {}", rex.getMessage());
                        throw new RuntimeException("Failed to authenticate with Upbolis: " + rex.getMessage());
                    }
                } else {
                    log.error("Error authenticating user with Upbolis: {}", ex.getMessage());
                    throw new RuntimeException("Failed to authenticate with Upbolis: " + ex.getMessage());
                }
            }

            if (loginResponse != null && loginResponse.getToken() != null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                user.setUpbolisToken(loginResponse.getToken());
                user.setUsername(email.split("@")[0]);
                userRepository.save(user);

                log.info("User {} authenticated with Upbolis successfully", userId);
                return loginResponse;
            }

            throw new RuntimeException("Failed to authenticate with Upbolis");
        } catch (Exception e) {
            log.error("Error authenticating user with Upbolis", e);
            throw new RuntimeException("Failed to authenticate with Upbolis: " + e.getMessage(), e);
        }
    }

    /**
     * Procesa un webhook de compra desde Upbolis
     */
    @Transactional
    public Purchase processPurchaseWebhook(String buyerUsername, String productName, Double amount, Long upbolisProductId, String transactionId) {
        try {
            // Encontrar el usuario por username
            Optional<User> userOpt = userRepository.findAll().stream()
                    .filter(u -> u.getUsername() != null && u.getUsername().equals(buyerUsername))
                    .findFirst();

            if (userOpt.isEmpty()) {
                throw new RuntimeException("Buyer user not found: " + buyerUsername);
            }

            User buyer = userOpt.get();

            // Attempt to resolve product using multiple strategies
            Product product = null;

            // 1) If upbolisProductId present and >0, try matching by upbolisProductId
            if (upbolisProductId != null && upbolisProductId > 0) {
                product = productRepository.findByUpbolisProductId(String.valueOf(upbolisProductId)).orElse(null);
                if (product != null) log.debug("Resolved product by upbolisProductId: {} -> {}", upbolisProductId, product.getId());
            }

            // 2) Try matching by product name or description (case-insensitive contains)
            if (product == null && productName != null && !productName.isBlank()) {
                String pn = productName.trim().toLowerCase();
                product = productRepository.findAll().stream()
                        .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(pn))
                                || (p.getDescription() != null && p.getDescription().toLowerCase().contains(pn)))
                        .findFirst().orElse(null);
                if (product != null) log.debug("Resolved product by name/description match: '{}' -> {}", productName, product.getId());
            }

            // 3) As a last resort, fetch products from Upbolis using system token and try to match by name
            if (product == null) {
                try {
                    String sysToken = upbolisApiClient.getSystemToken();
                    UpbolisProductDto[] remote = upbolisApiClient.getUserProducts(sysToken);
                    if (remote != null) {
                        String pn = productName != null ? productName.trim().toLowerCase() : null;
                        for (UpbolisProductDto rp : remote) {
                            if (rp == null) continue;
                            String remoteName = rp.getName() != null ? rp.getName().toLowerCase() : null;
                            String remoteTitle = rp.getProductName() != null ? rp.getProductName().toLowerCase() : null;
                            if (pn != null && ((remoteName != null && remoteName.contains(pn)) || (remoteTitle != null && remoteTitle.contains(pn)))) {
                                // Found a matching remote product; try to resolve locally by upbolis id
                                Long rid = rp.getProductId();
                                if (rid != null) {
                                    product = productRepository.findByUpbolisProductId(String.valueOf(rid)).orElse(null);
                                    if (product != null) {
                                        log.debug("Resolved product by fetching Upbolis account products and matching name -> local product {} (remote id={})", product.getId(), rid);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch products from Upbolis for webhook resolution: {}", e.getMessage());
                }
            }

            if (product == null) {
                throw new RuntimeException("Product not found in database (webhook product='" + productName + "', upbolis_id=" + upbolisProductId + ")");
            }

            // Try to find an existing purchase (prefer PENDING) for this user+product
            java.util.List<Purchase> userPurchases = purchaseRepository.findByUserId(buyer.getId());

            Product finalProduct = product;
            Purchase pending = userPurchases.stream()
                    .filter(p -> p.getProduct() != null && p.getProduct().getId().equals(finalProduct.getId()))
                    .filter(p -> p.getStatus() == PurchaseStatus.PENDING)
                    .sorted((a, b) -> (a.getCreatedDate() == null ? 0 : a.getCreatedDate().compareTo(b.getCreatedDate())))
                    .reduce((first, second) -> second).orElse(null); // get the most recent pending

            if (pending != null) {
                // Update the pending purchase to completed
                pending.setStatus(PurchaseStatus.COMPLETED);
                if (amount != null) pending.setPrice(amount);
                pending.setCurrency("USD");
                pending.setPurchasedAt(Instant.now());
                if (transactionId != null && !transactionId.isBlank()) pending.setExternalPaymentId(transactionId);
                else pending.setExternalPaymentId("UPBOLIS_" + System.currentTimeMillis());

                Purchase updated = purchaseRepository.save(pending);

                // Ensure owned product exists for this completed purchase
                try {
                    boolean alreadyOwned = ownedProductRepository.findByUserId(buyer.getId()).stream()
                            .anyMatch(op -> op.getProduct() != null && op.getProduct().getId().equals(finalProduct.getId()));

                    if (!alreadyOwned) {
                        com.UnderUpb.backendUnderUpb.entity.OwnedProduct op = com.UnderUpb.backendUnderUpb.entity.OwnedProduct.builder()
                                .user(buyer)
                                .product(product)
                                .purchase(updated)
                                .isActive(Boolean.TRUE)
                                .equipped(Boolean.FALSE)
                                .build();
                        ownedProductRepository.save(op);
                        log.debug("Created owned product (webhook) for user {} and product {} from purchase {}", buyer.getId(), product.getId(), updated.getId());
                    }
                } catch (Exception ex) {
                    log.warn("Failed to create owned product for webhook-updated purchase {}: {}", updated.getId(), ex.getMessage());
                }

                log.info("Purchase webhook reconciled and completed for user {} on product {} (purchase {})", buyerUsername, productName, updated.getId());
                return updated;
            }

            // If no pending purchase, but maybe an existing completed one exists - ensure owned product then return it
            Purchase existingCompleted = userPurchases.stream()
                    .filter(p -> p.getProduct() != null && p.getProduct().getId().equals(finalProduct.getId()))
                    .filter(p -> p.getStatus() == PurchaseStatus.COMPLETED)
                    .findFirst().orElse(null);

            if (existingCompleted != null) {
                try {
                    boolean alreadyOwned = ownedProductRepository.findByUserId(buyer.getId()).stream()
                            .anyMatch(op -> op.getProduct() != null && op.getProduct().getId().equals(finalProduct.getId()));
                    if (!alreadyOwned) {
                        com.UnderUpb.backendUnderUpb.entity.OwnedProduct op = com.UnderUpb.backendUnderUpb.entity.OwnedProduct.builder()
                                .user(buyer)
                                .product(product)
                                .purchase(existingCompleted)
                                .isActive(Boolean.TRUE)
                                .equipped(Boolean.FALSE)
                                .build();
                        ownedProductRepository.save(op);
                    }
                } catch (Exception ex) {
                    log.warn("Failed to create owned product for existing completed purchase {}: {}", existingCompleted.getId(), ex.getMessage());
                }
                log.info("Purchase webhook matched existing completed purchase {} for user {} product {}", existingCompleted.getId(), buyerUsername, productName);
                return existingCompleted;
            }

            // No existing purchase, create a new completed purchase
            Purchase purchase = Purchase.builder()
                    .user(buyer)
                    .product(product)
                    .quantity(1)
                    .price(amount)
                    .currency("USD")
                    .status(PurchaseStatus.COMPLETED)
                    .purchasedAt(Instant.now())
                    .externalPaymentId(transactionId != null && !transactionId.isBlank() ? transactionId : "UPBOLIS_" + System.currentTimeMillis())
                    .build();

            Purchase savedPurchase = purchaseRepository.save(purchase);

            // Create owned product record for this new purchase
            try {
                com.UnderUpb.backendUnderUpb.entity.OwnedProduct op = com.UnderUpb.backendUnderUpb.entity.OwnedProduct.builder()
                        .user(buyer)
                        .product(product)
                        .purchase(savedPurchase)
                        .isActive(Boolean.TRUE)
                        .equipped(Boolean.FALSE)
                        .build();
                ownedProductRepository.save(op);
            } catch (Exception ex) {
                log.warn("Failed to create owned product for new webhook purchase {}: {}", savedPurchase.getId(), ex.getMessage());
            }

            log.info("Purchase webhook processed successfully for user {} on product {}", buyerUsername, productName);

            return savedPurchase;
        } catch (Exception e) {
            log.error("Error processing purchase webhook: {}", e.getMessage());
            throw new RuntimeException("Failed to process purchase webhook: " + e.getMessage());
        }
    }

    /**
     * Verifica que un usuario tenga token válido de Upbolis
     */
    public boolean validateUserUpbolisToken(UUID userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (user.getUpbolisToken() == null || user.getUpbolisToken().isEmpty()) {
                return false;
            }

            // Validar que el token sea funcional intentando obtener un recurso
            upbolisApiClient.getUserProducts(user.getUpbolisToken());
            return true;
        } catch (Exception e) {
            log.warn("User {} has invalid Upbolis token: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el token de Upbolis de un usuario
     */
    public String getUserUpbolisToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getUpbolisToken() == null || user.getUpbolisToken().isEmpty()) {
            throw new RuntimeException("User has not authenticated with Upbolis");
        }

        return user.getUpbolisToken();
    }

    /**
     * Crea una compra en el sistema (el usuario debe tener token de Upbolis)
     */
    @Transactional
    public Purchase createPurchaseWithUpbolisVerification(UUID userId, UUID productId, Integer quantity) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            // Verificar que el usuario tiene un token válido en Upbolis
            if (!validateUserUpbolisToken(userId)) {
                throw new RuntimeException("User has not authenticated with Upbolis");
            }

            Purchase purchase = Purchase.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity != null ? quantity : 1)
                    .price(product.getPrice() * (quantity != null ? quantity : 1))
                    .currency(product.getCurrency() != null ? product.getCurrency() : "USD")
                    .status(PurchaseStatus.PENDING)
                    .purchasedAt(Instant.now())
                    .build();

            Purchase savedPurchase = purchaseRepository.save(purchase);

            log.info("Purchase created for user {} on product {} with Upbolis verification", userId, productId);

            return savedPurchase;
        } catch (Exception e) {
            log.error("Error creating purchase with Upbolis verification: {}", e.getMessage());
            throw new RuntimeException("Failed to create purchase: " + e.getMessage());
        }
    }
}
