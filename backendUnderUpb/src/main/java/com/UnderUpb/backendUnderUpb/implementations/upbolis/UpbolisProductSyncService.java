package com.UnderUpb.backendUnderUpb.implementations.upbolis;

import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisCreateProductDto;
import com.UnderUpb.backendUnderUpb.dto.upbolis.UpbolisProductDto;
import com.UnderUpb.backendUnderUpb.entity.Product;
import com.UnderUpb.backendUnderUpb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Slf4j
@Service
public class UpbolisProductSyncService {

    private final UpbolisApiClient upbolisApiClient;
    private final ProductRepository productRepository;
    private final TransactionTemplate txTemplate;

    public UpbolisProductSyncService(UpbolisApiClient upbolisApiClient, ProductRepository productRepository, PlatformTransactionManager txManager) {
        this.upbolisApiClient = upbolisApiClient;
        this.productRepository = productRepository;
        this.txTemplate = new TransactionTemplate(txManager);
    }

    @Value("${upbolis.system.token}")
    private String cachedSystemToken;

    /**
     * Sincroniza un producto local con Upbolis (crea o actualiza)
     * Runs in a new transaction to ensure repository operations succeed
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product syncProductToUpbolis(Product product) {
        try {
            String systemToken = getSystemToken();

            if (product.getUpbolisProductId() == null) {
                // Intentar buscar producto existente en Upbolis por nombre (evita duplicados en caso de races)
                try {
                    UpbolisProductDto[] sellerProducts = upbolisApiClient.getUserProducts(systemToken);
                    if (sellerProducts != null) {
                        for (UpbolisProductDto up : sellerProducts) {
                            if (up.getProductName() != null && up.getProductName().equals(product.getName())) {
                                if (up.getProductId() != null) {
                                    product.setUpbolisProductId(String.valueOf(up.getProductId()));

                                    Product savedProduct = txTemplate.execute(status -> {
                                        Product sp = productRepository.save(product);
                                        productRepository.flush();
                                        return sp;
                                    });

                                    log.info(product.toString());
                                    log.info("Found existing Upbolis product for {} with ID {}; will update instead of create", product.getName(), up.getProductId());

                                    // Verify persisted value by reloading
                                    try {
                                        Product reloaded = productRepository.findById(savedProduct.getId()).orElse(null);
                                        if (reloaded != null) log.debug("Reloaded product has upbolis ID after save+flush: {}", reloaded.getUpbolisProductId());
                                    } catch (Exception e) {
                                        log.debug("Could not reload product to verify persisted upbolis id: {}", e.getMessage());
                                    }

                                    return updateProductInUpbolis(savedProduct, systemToken);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("Could not fetch seller products for pre-check: {}", e.getMessage());
                }

                // Crear producto en Upbolis
                return createProductInUpbolis(product, systemToken);
            } else {
                // Actualizar precio en Upbolis
                return updateProductInUpbolis(product, systemToken);
            }
        } catch (Exception e) {
            log.error("Error syncing product {} to Upbolis: {}", product.getId(), e.getMessage());
            throw new RuntimeException("Failed to sync product to Upbolis: " + e.getMessage());
        }
    }

    /**
     * Crea un nuevo producto en Upbolis
     */
    @Transactional
    protected Product createProductInUpbolis(Product product, String systemToken) {
        try {
            UpbolisProductDto upbolisProduct = upbolisApiClient.createProduct(
                    systemToken,
                    product.getName(),
                    product.getPrice(),
                    product.getDescription()
            );

            // Guardar el ID del producto en Upbolis
            if (upbolisProduct != null && upbolisProduct.getProductId() != null) {
                product.setUpbolisProductId(String.valueOf(upbolisProduct.getProductId()));

                Product savedProduct = txTemplate.execute(status -> {
                    Product sp = productRepository.save(product);
                    productRepository.flush();
                    return sp;
                });

                log.info(savedProduct.toString());
                log.info("Product {} created in Upbolis with ID: {}", product.getName(), upbolisProduct.getProductId());

                try {
                    Product reloaded = productRepository.findById(savedProduct.getId()).orElse(null);
                    if (reloaded != null) log.debug("Reloaded product after create+flush has upbolis ID: {}", reloaded.getUpbolisProductId());
                } catch (Exception ex) {
                    log.debug("Could not reload product after create to verify persisted upbolis id: {}", ex.getMessage());
                }

                return savedProduct;
            }

            // Fallback: intentar extraer ID buscando en la lista del seller (por nombre)
            try {
                UpbolisProductDto[] sellerProducts = upbolisApiClient.getUserProducts(systemToken);
                if (sellerProducts != null) {
                    for (UpbolisProductDto p : sellerProducts) {
                        if (p.getProductName() != null && p.getProductName().equals(product.getName())) {
                            if (p.getProductId() != null) {
                                                product.setUpbolisProductId(String.valueOf(p.getProductId()));
                                Product savedProduct = txTemplate.execute(status -> {
                                    Product sp = productRepository.save(product);
                                    productRepository.flush();
                                    return sp;
                                });
                                log.info(product.toString());
                                log.info("Product {} matched in seller products; saved Upbolis ID {}", product.getName(), p.getProductId());
                                return savedProduct;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.debug("Fallback product id search failed: {}", ex.getMessage());
            }

            throw new RuntimeException("Failed to get product ID from Upbolis response");
        } catch (Exception e) {
            log.error("Error creating product in Upbolis", e);
            throw new RuntimeException("Failed to create product in Upbolis: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza el precio de un producto en Upbolis
     */
    @Transactional
    protected Product updateProductInUpbolis(Product product, String systemToken) {
        try {
            Long upbolisProductId = Long.parseLong(product.getUpbolisProductId());

            UpbolisCreateProductDto updateDto = UpbolisCreateProductDto.builder()
                    .name(product.getName())
                    .price(product.getPrice())
                    .description(product.getDescription())
                    .stock(99999999)
                    .isActive(true)
                    .build();

            upbolisApiClient.updateProduct(systemToken, upbolisProductId, updateDto);

            // flush and verify local state didn't regress within an explicit transaction
            try {
                txTemplate.execute(status -> {
                    productRepository.flush();
                    Product reloaded = productRepository.findById(product.getId()).orElse(null);
                    if (reloaded != null) log.debug("Reloaded product after update has upbolis ID: {}", reloaded.getUpbolisProductId());
                    return null;
                });
            } catch (Exception e) {
                log.debug("Could not flush/reload product after Upbolis update: {}", e.getMessage());
            }

            log.info("Product {} updated in Upbolis", product.getName());
            return product;
        } catch (NumberFormatException nfe) {
            log.error("Stored Upbolis product ID is not numeric: {}", product.getUpbolisProductId());
            throw new RuntimeException("Invalid Upbolis product ID: " + product.getUpbolisProductId());
        } catch (Exception e) {
            log.error("Error updating product in Upbolis", e);
            throw new RuntimeException("Failed to update product in Upbolis: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene un producto de Upbolis y lo sincroniza localmente
     */
    @Transactional
    public Product syncProductFromUpbolis(Long upbolisProductId) {
        try {
            String systemToken = getSystemToken();

            UpbolisProductDto upbolisProduct = upbolisApiClient.getProductDetails(systemToken, upbolisProductId);

            Optional<Product> existingProduct = productRepository.findByUpbolisProductId(String.valueOf(upbolisProductId));

            if (existingProduct.isPresent()) {
                Product product = existingProduct.get();
                product.setName(upbolisProduct.getProductName());
                product.setPrice(upbolisProduct.getPrice());
                product.setDescription(upbolisProduct.getDescription());

                return txTemplate.execute(status -> {
                    Product sp = productRepository.save(product);
                    productRepository.flush();
                    return sp;
                });
            }

            // Si no existe localmente, crear nuevo
            Product newProduct = Product.builder()
                    .name(upbolisProduct.getProductName())
                    .price(upbolisProduct.getPrice())
                    .description(upbolisProduct.getDescription())
                    .upbolisProductId(String.valueOf(upbolisProductId))
                    .type("SKIN")
                    .currency("USD")
                    .build();

            return txTemplate.execute(status -> {
                Product sp = productRepository.save(newProduct);
                productRepository.flush();
                return sp;
            });
        } catch (Exception e) {
            log.error("Error syncing product from Upbolis: {}", e.getMessage());
            throw new RuntimeException("Failed to sync product from Upbolis: " + e.getMessage());
        }
    }

    /**
     * Obtiene el token del sistema
     */
    private String getSystemToken() {
        try {
            return upbolisApiClient.getSystemToken();
        } catch (Exception e) {
            log.error("Failed to get system token: {}", e.getMessage());
            throw new RuntimeException("Failed to get system token: " + e.getMessage());
        }
    }

    /**
     * Sincroniza todos los productos del usuario en Upbolis
     */
    @Transactional
    public void syncAllProductsToUpbolis() {
        try {
            String systemToken = getSystemToken();
            UpbolisProductDto[] upbolisProducts = upbolisApiClient.getUserProducts(systemToken);

            for (UpbolisProductDto upbolisProduct : upbolisProducts) {
                syncProductFromUpbolis(upbolisProduct.getProductId());
            }

            log.info("All products synced from Upbolis successfully");
        } catch (Exception e) {
            log.error("Error syncing all products from Upbolis: {}", e.getMessage());
            throw new RuntimeException("Failed to sync all products: " + e.getMessage());
        }
    }

    /**
     * Elimina un producto en Upbolis
     */
    @Transactional
    public void removeProductFromUpbolis(Product product) {
        try {
            if (product.getUpbolisProductId() == null) {
                log.info("Product {} has no Upbolis ID; nothing to remove", product.getName());
                return;
            }

            String systemToken = getSystemToken();
            Long upbolisProductId = Long.parseLong(product.getUpbolisProductId());
            upbolisApiClient.deleteProduct(systemToken, upbolisProductId);
            log.info("Requested deletion of product {} (Upbolis ID {})", product.getName(), upbolisProductId);
        } catch (Exception e) {
            log.warn("Error removing product from Upbolis: {}", e.getMessage());
            // No propagar la excepción para no afectar la transacción que originó la eliminación
        }
    }
}
