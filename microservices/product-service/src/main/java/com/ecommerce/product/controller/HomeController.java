package com.ecommerce.product.controller;

import com.ecommerce.product.model.dto.ApiResponse;
import com.ecommerce.product.model.dto.HomeProductsDTO;
import com.ecommerce.product.model.dto.ProductResponse;
import com.ecommerce.product.model.entity.ProductCategory;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeController {
    
    private final ProductService productService;

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<HomeProductsDTO>> getHomeFeaturedProducts() {
        try {
            log.info("Getting home featured products");
            
            HomeProductsDTO homeProducts = new HomeProductsDTO();
            
            // 获取推荐水果
            List<ProductResponse> featuredFruits = productService.getFeaturedProductsByCategory(ProductCategory.FRUIT, 8);
            homeProducts.setFeaturedFruits(featuredFruits);
            
            // 获取推荐蔬菜
            List<ProductResponse> featuredVegetables = productService.getFeaturedProductsByCategory(ProductCategory.VEGETABLE, 8);
            homeProducts.setFeaturedVegetables(featuredVegetables);
            
            return ResponseEntity.ok(ApiResponse.success("Home products retrieved successfully", homeProducts));
        } catch (Exception e) {
            log.error("Error getting home featured products: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error retrieving home products: " + e.getMessage()));
        }
    }
}
