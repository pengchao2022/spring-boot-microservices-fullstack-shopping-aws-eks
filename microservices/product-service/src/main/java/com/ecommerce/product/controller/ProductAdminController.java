package com.ecommerce.product.controller;

import com.ecommerce.product.model.entity.Product;
import com.ecommerce.product.model.entity.elasticsearch.EsProduct;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.elasticsearch.EsProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ProductAdminController {
    
    private final EsProductRepository esProductRepository;
    private final ProductRepository productRepository;
    
    @PostMapping("/products/reindex")
    public ResponseEntity<String> reindexProducts() {
        try {
            log.info("Starting manual reindexing of products...");
            
            // 清空现有索引
            esProductRepository.deleteAll();
            
            // 从数据库加载所有产品
            List<Product> products = productRepository.findAll();
            log.info("Found {} products to index", products.size());
            
            // 转换为 Elasticsearch 实体
            List<EsProduct> esProducts = products.stream()
                .map(this::convertToEsProduct)
                .collect(Collectors.toList());
            
            // 保存到 Elasticsearch
            esProductRepository.saveAll(esProducts);
            
            String message = String.format("Successfully reindexed %d products to Elasticsearch", esProducts.size());
            log.info(message);
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            log.error("Reindexing failed", e);
            return ResponseEntity.internalServerError()
                .body("Reindexing failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/products/index-status")
    public ResponseEntity<String> getIndexStatus() {
        try {
            long dbCount = productRepository.count();
            long esCount = esProductRepository.count();
            
            String status = String.format(
                "Database products: %d, Elasticsearch products: %d", 
                dbCount, esCount
            );
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error getting index status: " + e.getMessage());
        }
    }
    
    private EsProduct convertToEsProduct(Product product) {
        EsProduct esProduct = new EsProduct();
        
        // 设置基础字段
        esProduct.setId(product.getId());
        esProduct.setName(product.getName());
        esProduct.setEnglishName(product.getEnglishName());
        esProduct.setDescription(product.getDescription());
        esProduct.setShortDescription(product.getShortDescription());
        
        // 修复类型转换问题 - categoryType: ProductCategory枚举 → String
        if (product.getCategoryType() != null) {
            esProduct.setCategoryType(product.getCategoryType().name());
        }
        
        // 修复类型转换问题 - basePrice: BigDecimal → Double
        if (product.getBasePrice() != null) {
            esProduct.setBasePrice(product.getBasePrice().doubleValue());
        }
        
        // 修复类型转换问题 - tags: String → List<String>
        esProduct.setTags(convertTagsToList(product.getTags()));
        
        // 修复类型转换问题 - imageUrls: String → List<String>
        esProduct.setImageUrls(convertImageUrlsToList(product.getImageUrls()));
        
        // 设置其他字段
        esProduct.setWeightUnit(product.getWeightUnit());
        esProduct.setOrigin(product.getOrigin());
        esProduct.setStorageMethod(product.getStorageMethod());
        esProduct.setShelfLife(product.getShelfLife());
        esProduct.setNutritionalInfo(product.getNutritionalInfo());
        esProduct.setMainImageUrl(product.getMainImageUrl());
        esProduct.setIsFeatured(product.getIsFeatured());
        esProduct.setIsActive(product.getIsActive());
        esProduct.setSortOrder(product.getSortOrder());
        esProduct.setSeason(product.getSeason());
        esProduct.setTasteDescription(product.getTasteDescription());
        esProduct.setGrowingMethod(product.getGrowingMethod());
        esProduct.setCertification(product.getCertification());
        esProduct.setViewCount(product.getViewCount());
        esProduct.setSalesCount(product.getSalesCount());
        
        // 修复：LocalDateTime 转 LocalDate
        if (product.getCreatedAt() != null) {
            esProduct.setCreatedAt(product.getCreatedAt().toLocalDate());
        }
        if (product.getUpdatedAt() != null) {
            esProduct.setUpdatedAt(product.getUpdatedAt().toLocalDate());
        }
        
        // 苹果特有字段
        esProduct.setSweetnessLevel(product.getSweetnessLevel());
        esProduct.setCrunchinessLevel(product.getCrunchinessLevel());
        esProduct.setAppleVariety(product.getAppleVariety());
        esProduct.setHarvestSeason(product.getHarvestSeason());
        
        // 猕猴桃特有字段
        esProduct.setAcidityLevel(product.getAcidityLevel());
        esProduct.setKiwiVariety(product.getKiwiVariety());
        esProduct.setVitaminCContent(product.getVitaminCContent());
        esProduct.setSkinType(product.getSkinType());
        
        // 搜索相关字段
        esProduct.setNameSuggest(generateNameSuggest(product.getName()));
        esProduct.setPriceRange(calculatePriceRange(product.getBasePrice()));
        esProduct.setSearchText(generateSearchText(product));
        
        return esProduct;
    }
    
    /**
     * 将逗号分隔的标签字符串转换为List<String>
     */
    private List<String> convertTagsToList(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(tags.split("\\s*,\\s*"));
    }
    
    /**
     * 将逗号分隔的图片URL字符串转换为List<String>
     */
    private List<String> convertImageUrlsToList(String imageUrls) {
        if (imageUrls == null || imageUrls.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(imageUrls.split("\\s*,\\s*"));
    }
    
    /**
     * 生成搜索建议
     */
    private String generateNameSuggest(String name) {
        if (name == null) {
            return "";
        }
        return name.toLowerCase();
    }
    
    /**
     * 计算价格范围
     */
    private Double calculatePriceRange(java.math.BigDecimal basePrice) {
        if (basePrice == null) {
            return 0.0;
        }
        double price = basePrice.doubleValue();
        if (price < 10) return 1.0;      // 低价位
        else if (price < 50) return 2.0; // 中价位
        else return 3.0;                 // 高价位
    }
    
    /**
     * 生成全文搜索文本
     */
    private String generateSearchText(Product product) {
        StringBuilder searchText = new StringBuilder();
        
        if (product.getName() != null) {
            searchText.append(product.getName()).append(" ");
        }
        if (product.getEnglishName() != null) {
            searchText.append(product.getEnglishName()).append(" ");
        }
        if (product.getDescription() != null) {
            searchText.append(product.getDescription()).append(" ");
        }
        if (product.getOrigin() != null) {
            searchText.append(product.getOrigin()).append(" ");
        }
        if (product.getTags() != null) {
            searchText.append(product.getTags()).append(" ");
        }
        if (product.getAppleVariety() != null) {
            searchText.append(product.getAppleVariety()).append(" ");
        }
        if (product.getKiwiVariety() != null) {
            searchText.append(product.getKiwiVariety()).append(" ");
        }
        
        return searchText.toString().trim();
    }
}