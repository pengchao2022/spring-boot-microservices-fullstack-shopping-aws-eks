package com.ecommerce.product.model.entity.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "ecommerce_products")
public class EsProduct {
    
    @Id
    private Long id;
    
    // 基础信息字段
    @Field(type = FieldType.Text)
    private String name;
    
    @Field(type = FieldType.Keyword)
    private String englishName;
    
    @Field(type = FieldType.Text)
    private String description;
    
    @Field(type = FieldType.Text)
    private String shortDescription;
    
    @Field(type = FieldType.Keyword)
    private String categoryType;
    
    @Field(type = FieldType.Double)
    private Double basePrice;
    
    @Field(type = FieldType.Keyword)
    private String weightUnit;
    
    @Field(type = FieldType.Keyword)
    private String origin;
    
    @Field(type = FieldType.Keyword)
    private String storageMethod;
    
    @Field(type = FieldType.Keyword)
    private String shelfLife;
    
    @Field(type = FieldType.Text)
    private String nutritionalInfo;
    
    @Field(type = FieldType.Keyword)
    private String mainImageUrl;
    
    @Field(type = FieldType.Keyword)
    private List<String> imageUrls;
    
    @Field(type = FieldType.Boolean)
    private Boolean isFeatured;
    
    @Field(type = FieldType.Boolean)
    private Boolean isActive;
    
    @Field(type = FieldType.Integer)
    private Integer sortOrder;
    
    @Field(type = FieldType.Keyword)
    private List<String> tags;
    
    @Field(type = FieldType.Keyword)
    private String season;
    
    @Field(type = FieldType.Text)
    private String tasteDescription;
    
    @Field(type = FieldType.Keyword)
    private String growingMethod;
    
    @Field(type = FieldType.Keyword)
    private String certification;
    
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
    @Field(type = FieldType.Integer)
    private Integer salesCount;
    
    // 时间字段 - 修改为 LocalDate
    @Field(type = FieldType.Date)
    private LocalDate createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDate updatedAt;
    
    // 苹果特有字段
    @Field(type = FieldType.Integer)
    private Integer sweetnessLevel;
    
    @Field(type = FieldType.Integer)
    private Integer crunchinessLevel;
    
    @Field(type = FieldType.Keyword)
    private String appleVariety;
    
    @Field(type = FieldType.Keyword)
    private String harvestSeason;
    
    // 猕猴桃特有字段
    @Field(type = FieldType.Integer)
    private Integer acidityLevel;
    
    @Field(type = FieldType.Keyword)
    private String kiwiVariety;
    
    @Field(type = FieldType.Keyword)
    private String vitaminCContent;
    
    @Field(type = FieldType.Keyword)
    private String skinType;
    
    // 搜索建议字段
    @Field(type = FieldType.Text)
    private String nameSuggest;
    
    // 用于聚合的字段
    @Field(type = FieldType.Double)
    private Double priceRange;
    
    // 全文搜索字段
    @Field(type = FieldType.Text)
    private String searchText;
}