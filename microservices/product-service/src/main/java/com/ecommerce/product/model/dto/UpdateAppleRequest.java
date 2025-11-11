// UpdateAppleRequest.java
package com.ecommerce.product.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateAppleRequest {
    private String name;
    private String description;
    private String shortDescription;
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private String weightUnit;
    private String mainImageUrl;
    private String origin;
    private Boolean isFeatured;
    private Integer sortOrder;
    private Integer sweetnessLevel;
    private Integer crunchinessLevel;
    private String appleVariety;
    private String harvestSeason;
    private String storageMethod;
    private String shelfLife;
    private String nutritionalInfo;
    private String tags;
    private String season;
}