package com.UnderUpb.backendUnderUpb.repository;

import com.UnderUpb.backendUnderUpb.entity.OwnedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnedProductRepository extends JpaRepository<OwnedProduct, UUID> {
    List<OwnedProduct> findByUserId(UUID userId);
    List<OwnedProduct> findByProductId(UUID productId);
    Optional<OwnedProduct> findByUserIdAndEquippedTrue(UUID userId);
}
