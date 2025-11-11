package com.ecommerce.product.model.entity.elasticsearch;

import com.ecommerce.product.model.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EsProductConverter {
    
    public EsProduct convertToEsProduct(Product product) {
        if (product == null) {
            return null;
        }
        
        String searchText = buildSearchText(product);
        
        return EsProduct.builder()
            .id(product.getId())
            .name(product.getName())
            .englishName(product.getEnglishName())
            .description(product.getDescription())
            .shortDescription(product.getShortDescription())
            .categoryType(product.getCategoryType() != null ? product.getCategoryType().name() : null)
            .basePrice(convertToDouble(product.getBasePrice()))
            .weightUnit(product.getWeightUnit())
            .origin(product.getOrigin())
            .storageMethod(product.getStorageMethod())
            .shelfLife(product.getShelfLife())
            .nutritionalInfo(product.getNutritionalInfo())
            .mainImageUrl(product.getMainImageUrl())
            .imageUrls(convertToImageUrls(product.getImageUrls()))
            .isFeatured(product.getIsFeatured())
            .isActive(product.getIsActive())
            .sortOrder(product.getSortOrder())
            .tags(convertToTags(product.getTags()))
            .season(product.getSeason())
            .tasteDescription(product.getTasteDescription())
            .growingMethod(product.getGrowingMethod())
            .certification(product.getCertification())
            .viewCount(product.getViewCount())
            .salesCount(product.getSalesCount())
            // 修复：LocalDateTime 转 LocalDate
            .createdAt(convertToLocalDate(product.getCreatedAt()))
            .updatedAt(convertToLocalDate(product.getUpdatedAt()))
            .sweetnessLevel(product.getSweetnessLevel())
            .crunchinessLevel(product.getCrunchinessLevel())
            .appleVariety(product.getAppleVariety())
            .harvestSeason(product.getHarvestSeason())
            .acidityLevel(product.getAcidityLevel())
            .kiwiVariety(product.getKiwiVariety())
            .vitaminCContent(product.getVitaminCContent())
            .skinType(product.getSkinType())
            .nameSuggest(product.getName())
            .priceRange(calculatePriceRange(convertToDouble(product.getBasePrice())))
            .searchText(searchText)
            .build();
    }
    
    /**
     * 将 LocalDateTime 转换为 LocalDate
     */
    private LocalDate convertToLocalDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate();
    }
    
    /**
     * 将 BigDecimal 转换为 Double
     */
    private Double convertToDouble(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.doubleValue();
    }
    
    /**
     * 将 String imageUrls 转换为 List<String>
     */
    private List<String> convertToImageUrls(String imageUrls) {
        if (imageUrls == null || imageUrls.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(imageUrls.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }
    
    /**
     * 将 String tags 转换为 List<String>
     */
    private List<String> convertToTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }
    
    private String buildSearchText(Product product) {
        StringBuilder sb = new StringBuilder();
        
        if (product.getName() != null) sb.append(product.getName()).append(" ");
        if (product.getDescription() != null) sb.append(product.getDescription()).append(" ");
        if (product.getShortDescription() != null) sb.append(product.getShortDescription()).append(" ");
        if (product.getTasteDescription() != null) sb.append(product.getTasteDescription()).append(" ");
        if (product.getOrigin() != null) sb.append(product.getOrigin()).append(" ");
        if (product.getAppleVariety() != null) sb.append(product.getAppleVariety()).append(" ");
        if (product.getKiwiVariety() != null) sb.append(product.getKiwiVariety()).append(" ");
        if (product.getTags() != null) sb.append(product.getTags()).append(" ");
        
        return sb.toString().trim();
    }
    
    private Double calculatePriceRange(Double price) {
        if (price == null) return 0.0;
        if (price < 10) return 0.0;
        else if (price < 20) return 10.0;
        else if (price < 50) return 20.0;
        else if (price < 100) return 50.0;
        else return 100.0;
    }
    
    public List<EsProduct> convertToEsProducts(List<Product> products) {
        return products.stream()
            .map(this::convertToEsProduct)
            .collect(Collectors.toList());
    }
}