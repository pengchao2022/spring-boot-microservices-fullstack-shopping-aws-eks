package com.ecommerce.product.controller;

import com.ecommerce.product.model.dto.ApiResponse;
import com.ecommerce.product.model.dto.ProductDetailDTO;
import com.ecommerce.product.model.dto.ProductResponse;
import com.ecommerce.product.model.entity.ProductCategory;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    /**
     * 获取苹果分类的所有产品
     */
    @GetMapping("/apple-category")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAppleCategoryProducts() {
        try {
            log.info("Getting all apple category products");
            List<ProductResponse> apples = productService.getAppleCategoryProducts();
            return ResponseEntity.ok(ApiResponse.success("Apple products found", apples));
        } catch (Exception e) {
            log.error("Error getting apple products: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error getting apple products: " + e.getMessage()));
        }
    }

    /**
     * 获取推荐的苹果产品
     */
    @GetMapping("/apple-category/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedApples(
            @RequestParam(defaultValue = "6") int limit) {
        try {
            log.info("Getting featured apples with limit: {}", limit);
            List<ProductResponse> featuredApples = productService.getFeaturedApples(limit);
            return ResponseEntity.ok(ApiResponse.success("Featured apples found", featuredApples));
        } catch (Exception e) {
            log.error("Error getting featured apples: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error getting featured apples: " + e.getMessage()));
        }
    }

    /**
     * 获取苹果详情（专用接口，包含苹果特有信息）
     */
    @GetMapping("/apple-category/{englishName}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getAppleDetail(
            @PathVariable String englishName) {
        try {
            log.info("Getting apple detail by english name: {}", englishName);
            ProductDetailDTO appleDetail = productService.getAppleDetailByEnglishName(englishName);
            return ResponseEntity.ok(ApiResponse.success("Apple product found", appleDetail));
        } catch (Exception e) {
            log.error("Error getting apple product: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Apple product not found: " + e.getMessage()));
        }
    }

    /**
     * 根据英文名称获取产品详情
     */
    @GetMapping("/{englishName}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductByEnglishName(
            @PathVariable String englishName) {
        try {
            log.info("Getting product by english name: {}", englishName);
            ProductDetailDTO productDetail = productService.getProductDetailByEnglishName(englishName);
            return ResponseEntity.ok(ApiResponse.success("Product found", productDetail));
        } catch (Exception e) {
            log.error("Error getting product: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Product not found: " + e.getMessage()));
        }
    }

    /**
     * 根据分类获取产品
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @PathVariable String category) {
        try {
            log.info("Getting products by category: {}", category);
            ProductCategory productCategory = ProductCategory.valueOf(category.toUpperCase());
            List<ProductResponse> products = productService.getProductsByCategory(productCategory);
            return ResponseEntity.ok(ApiResponse.success("Products found", products));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid category: " + category));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error getting products: " + e.getMessage()));
        }
    }

    /**
     * 获取推荐产品
     */
    @GetMapping("/featured/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedProducts(
            @PathVariable String category,
            @RequestParam(defaultValue = "8") int limit) {
        try {
            ProductCategory productCategory = ProductCategory.valueOf(category.toUpperCase());
            List<ProductResponse> products = productService.getFeaturedProductsByCategory(productCategory, limit);
            return ResponseEntity.ok(ApiResponse.success("Featured products found", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error getting featured products: " + e.getMessage()));
        }
    }

    // ==================== 搜索端点 ====================

    /**
     * 搜索产品（支持原始中文参数）
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // 处理中文参数编码问题
            String decodedKeyword = decodeChineseParameter(keyword);
            log.info("Searching products with keyword: '{}' (decoded: '{}'), page: {}, size: {}", 
                    keyword, decodedKeyword, page, size);
            
            List<ProductResponse> products = productService.searchProducts(decodedKeyword, page, size);
            
            if (products.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("No products found for keyword: " + decodedKeyword, products));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Products found for keyword: " + decodedKeyword, products));
        } catch (Exception e) {
            log.error("Error searching products with keyword '{}': {}", keyword, e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error searching products: " + e.getMessage()));
        }
    }

    /**
     * 搜索产品（简化版本，只包含关键词参数，支持原始中文）
     */
    @GetMapping("/search/simple")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProductsSimple(
            @RequestParam String keyword) {
        try {
            // 处理中文参数编码问题
            String decodedKeyword = decodeChineseParameter(keyword);
            log.info("Simple search products with keyword: '{}' (decoded: '{}')", keyword, decodedKeyword);
            
            List<ProductResponse> products = productService.searchProducts(decodedKeyword, 0, 20);
            
            if (products.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("No products found for keyword: " + decodedKeyword, products));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Products found for keyword: " + decodedKeyword, products));
        } catch (Exception e) {
            log.error("Error in simple search with keyword '{}': {}", keyword, e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error searching products: " + e.getMessage()));
        }
    }

    /**
     * 解码中文参数
     * 处理前端可能发送的URL编码或原始中文参数
     */
    private String decodeChineseParameter(String param) {
        if (param == null || param.trim().isEmpty()) {
            return param;
        }
        
        try {
            // 如果参数看起来是URL编码的（包含%符号），则进行解码
            if (param.contains("%")) {
                String decoded = URLDecoder.decode(param, StandardCharsets.UTF_8.name());
                log.debug("Decoded parameter from '{}' to '{}'", param, decoded);
                return decoded;
            }
            
            // 如果参数已经是原始中文，直接返回
            return param;
            
        } catch (Exception e) {
            log.warn("Failed to decode parameter '{}', using original: {}", param, e.getMessage());
            return param;
        }
    }
}