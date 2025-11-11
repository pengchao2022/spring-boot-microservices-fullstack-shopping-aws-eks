package com.ecommerce.product.controller;

import com.ecommerce.product.model.dto.ApiResponse;
import com.ecommerce.product.model.entity.Category;
import com.ecommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        try {
            log.info("Getting all categories");
            List<Category> categories = categoryService.getAllActiveCategories();
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            log.error("Error getting categories: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving categories: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        try {
            log.info("Getting category by id: {}", id);
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
        } catch (Exception e) {
            log.error("Error getting category: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Category not found: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Category>>> getCategoriesByType(@PathVariable String type) {
        try {
            log.info("Getting categories by type: {}", type);
            List<Category> categories = categoryService.getCategoriesByType(type);
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            log.error("Error getting categories by type: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving categories: " + e.getMessage()));
        }
    }
}
