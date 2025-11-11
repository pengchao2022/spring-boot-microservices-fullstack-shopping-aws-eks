package com.ecommerce.product.model.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product")
public class Product extends BaseEntity {
    @Column(nullable = false)
    private String name;
    
    @Column(name = "english_name", unique = true, nullable = false)
    private String englishName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "short_description", length = 500)
    private String shortDescription;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    private ProductCategory categoryType = ProductCategory.FRUIT;
    
    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "weight_unit")
    private String weightUnit = "斤";
    
    private String origin;
    
    @Column(name = "storage_method", columnDefinition = "TEXT")
    private String storageMethod;
    
    @Column(name = "shelf_life")
    private String shelfLife;
    
    @Column(name = "nutritional_info", columnDefinition = "JSON")
    private String nutritionalInfo;
    
    @Column(name = "main_image_url")
    private String mainImageUrl;
    
    @Column(name = "image_urls", columnDefinition = "JSON")
    private String imageUrls;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(columnDefinition = "JSON")
    private String tags;
    
    private String season;
    
    @Column(name = "taste_description")
    private String tasteDescription;
    
    @Column(name = "growing_method")
    private String growingMethod;
    
    private String certification;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "sales_count")
    private Integer salesCount = 0;
    
    // 苹果专用字段
    @Column(name = "sweetness_level")
    private Integer sweetnessLevel;
    
    @Column(name = "crunchiness_level")
    private Integer crunchinessLevel;
    
    @Column(name = "apple_variety")
    private String appleVariety;
    
    @Column(name = "harvest_season")
    private String harvestSeason;
    
    // 猕猴桃专用字段
    @Column(name = "acidity_level")
    private Integer acidityLevel;
    
    @Column(name = "kiwi_variety")
    private String kiwiVariety;
    
    @Column(name = "vitamin_c_content")
    private String vitaminCContent;
    
    @Column(name = "skin_type")
    private String skinType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> variants;
    
    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public ProductCategory getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(ProductCategory categoryType) {
        this.categoryType = categoryType;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getStorageMethod() {
        return storageMethod;
    }

    public void setStorageMethod(String storageMethod) {
        this.storageMethod = storageMethod;
    }

    public String getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    public String getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(String nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getTasteDescription() {
        return tasteDescription;
    }

    public void setTasteDescription(String tasteDescription) {
        this.tasteDescription = tasteDescription;
    }

    public String getGrowingMethod() {
        return growingMethod;
    }

    public void setGrowingMethod(String growingMethod) {
        this.growingMethod = growingMethod;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    // 苹果专用字段的 getter 和 setter
    public Integer getSweetnessLevel() {
        return sweetnessLevel;
    }

    public void setSweetnessLevel(Integer sweetnessLevel) {
        this.sweetnessLevel = sweetnessLevel;
    }

    public Integer getCrunchinessLevel() {
        return crunchinessLevel;
    }

    public void setCrunchinessLevel(Integer crunchinessLevel) {
        this.crunchinessLevel = crunchinessLevel;
    }

    public String getAppleVariety() {
        return appleVariety;
    }

    public void setAppleVariety(String appleVariety) {
        this.appleVariety = appleVariety;
    }

    public String getHarvestSeason() {
        return harvestSeason;
    }

    public void setHarvestSeason(String harvestSeason) {
        this.harvestSeason = harvestSeason;
    }

    // 猕猴桃专用字段的 getter 和 setter
    public Integer getAcidityLevel() {
        return acidityLevel;
    }

    public void setAcidityLevel(Integer acidityLevel) {
        this.acidityLevel = acidityLevel;
    }

    public String getKiwiVariety() {
        return kiwiVariety;
    }

    public void setKiwiVariety(String kiwiVariety) {
        this.kiwiVariety = kiwiVariety;
    }

    public String getVitaminCContent() {
        return vitaminCContent;
    }

    public void setVitaminCContent(String vitaminCContent) {
        this.vitaminCContent = vitaminCContent;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }
}