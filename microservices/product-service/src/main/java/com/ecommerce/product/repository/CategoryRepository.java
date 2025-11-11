package com.ecommerce.product.repository;

import com.ecommerce.product.model.entity.Category;
import com.ecommerce.product.model.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrueOrderBySortOrderAsc();
    List<Category> findByTypeAndIsActiveTrueOrderBySortOrderAsc(CategoryType type);
    List<Category> findByParentIsNullAndIsActiveTrueOrderBySortOrderAsc();
    List<Category> findByParentIdAndIsActiveTrueOrderBySortOrderAsc(Long parentId);
}
