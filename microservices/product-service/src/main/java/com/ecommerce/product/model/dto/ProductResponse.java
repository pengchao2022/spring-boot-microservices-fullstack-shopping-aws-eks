package com.ecommerce.product.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String englishName;
    private String description;
    private String shortDescription;
    private String categoryType;
    private BigDecimal basePrice;
    private String weightUnit;
    private String origin;
    private String storageMethod;
    private String shelfLife;
    private String nutritionalInfo;
    private String mainImageUrl;
    private String imageUrls;
    private Boolean isFeatured;
    private Boolean isActive;
    private Integer sortOrder;
    private String tags;
    private String season;
    private String tasteDescription;
    private String growingMethod;
    private String certification;
    private Integer viewCount;
    private Integer salesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductVariantResponse> variants;
    
    // 苹果专用字段
    private Integer sweetnessLevel;
    private Integer crunchinessLevel;
    private String appleVariety;
    private String harvestSeason;
}