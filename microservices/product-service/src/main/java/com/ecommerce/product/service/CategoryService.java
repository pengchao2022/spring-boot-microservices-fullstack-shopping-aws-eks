package com.ecommerce.product.service;

import com.ecommerce.product.model.entity.Category;
import com.ecommerce.product.model.entity.CategoryType;
import com.ecommerce.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public List<Category> getCategoriesByType(String type) {
        try {
            CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
            return categoryRepository.findByTypeAndIsActiveTrueOrderBySortOrderAsc(categoryType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category type: " + type);
        }
    }
}
