// src/main/java/com/ecommerce/product/controller/KiwiCategoryController.java
package com.ecommerce.product.controller;

import com.ecommerce.product.model.dto.ApiResponse;
import com.ecommerce.product.model.dto.KiwiProductResponse;
import com.ecommerce.product.model.dto.ProductDetailDTO;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KiwiCategoryController {
    private final ProductService productService;

    /**
     * 获取猕猴桃分类的所有产品
     */
    @GetMapping("/kiwi-category")
    public ResponseEntity<ApiResponse<List<KiwiProductResponse>>> getKiwiCategoryProducts() {
        try {
            log.info("Getting all kiwi category products");
            List<KiwiProductResponse> kiwis = productService.getKiwiCategoryProducts();
            return ResponseEntity.ok(ApiResponse.success("Kiwi products found", kiwis));
        } catch (Exception e) {
            log.error("Error getting kiwi products: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error getting kiwi products: " + e.getMessage()));
        }
    }

    /**
     * 获取推荐的猕猴桃产品
     */
    @GetMapping("/kiwi-category/featured")
    public ResponseEntity<ApiResponse<List<KiwiProductResponse>>> getFeaturedKiwis(
            @RequestParam(defaultValue = "6") int limit) {
        try {
            log.info("Getting featured kiwis with limit: {}", limit);
            List<KiwiProductResponse> featuredKiwis = productService.getFeaturedKiwis(limit);
            return ResponseEntity.ok(ApiResponse.success("Featured kiwis found", featuredKiwis));
        } catch (Exception e) {
            log.error("Error getting featured kiwis: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error getting featured kiwis: " + e.getMessage()));
        }
    }

    /**
     * 获取猕猴桃详情（专用接口，包含猕猴桃特有信息）
     */
    @GetMapping("/kiwi-category/{englishName}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getKiwiDetail(
            @PathVariable String englishName) {
        try {
            log.info("Getting kiwi detail by english name: {}", englishName);
            ProductDetailDTO kiwiDetail = productService.getKiwiDetailByEnglishName(englishName);
            return ResponseEntity.ok(ApiResponse.success("Kiwi product found", kiwiDetail));
        } catch (Exception e) {
            log.error("Error getting kiwi product: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Kiwi product not found: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取猕猴桃详情
     */
    @GetMapping("/kiwi-category/id/{id}")
    public ResponseEntity<ApiResponse<KiwiProductResponse>> getKiwiById(@PathVariable Long id) {
        try {
            log.info("Getting kiwi product by id: {}", id);
            KiwiProductResponse kiwi = productService.getKiwiProductById(id);
            return ResponseEntity.ok(ApiResponse.success("Kiwi product found", kiwi));
        } catch (Exception e) {
            log.error("Error getting kiwi product by id: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Kiwi product not found: " + e.getMessage()));
        }
    }

    /**
     * 测试端点
     */
    @GetMapping("/kiwi-category/test")
    public ResponseEntity<ApiResponse<String>> testKiwiEndpoint() {
        log.info("Kiwi test endpoint called");
        return ResponseEntity.ok(ApiResponse.success("Kiwi endpoint is working", "Test successful"));
    }
}