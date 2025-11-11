package com.ecommerce.product.repository;

import com.ecommerce.product.model.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductIdAndIsActiveTrueOrderBySortOrderAsc(Long productId);
    List<ProductVariant> findByProductIdOrderBySortOrderAsc(Long productId);
}
