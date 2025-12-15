package com.UnderUpb.backendUnderUpb.repository;

import com.UnderUpb.backendUnderUpb.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    List<Purchase> findByUserId(UUID userId);
    List<Purchase> findByProductId(UUID productId);

    @Query("""
        SELECT p
        FROM Purchase p
        WHERE p.product.id = :productId
          AND p.user.id = :userId
          AND p.status = 'PENDING'
        ORDER BY p.createdDate DESC
    """)
    Optional<Purchase> findLatestPendingByProductIdAndUserId(
            @Param("productId") UUID productId,
            @Param("userId") UUID userId
    );

    @Query("""
        SELECT p
        FROM Purchase p
        WHERE p.product.id = :productId
          AND p.user.id = :userId
          AND p.status = 'COMPLETED'
    """)
    Optional<Purchase> findCompletedByProductIdAndUserId(
            @Param("productId") UUID productId,
            @Param("userId") UUID userId
    );


}
