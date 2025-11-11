// src/main/java/com/ecommerce/product/model/dto/KiwiProductResponse.java
package com.ecommerce.product.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KiwiProductResponse extends ProductResponse {
    
    @JsonProperty("acidityLevel")
    private Integer acidityLevel;
    
    @JsonProperty("kiwiVariety")
    private String kiwiVariety;
    
    @JsonProperty("vitaminCContent")
    private String vitaminCContent;
    
    @JsonProperty("skinType")
    private String skinType;
    
    // 构造方法
    public KiwiProductResponse() {
        super();
    }
}