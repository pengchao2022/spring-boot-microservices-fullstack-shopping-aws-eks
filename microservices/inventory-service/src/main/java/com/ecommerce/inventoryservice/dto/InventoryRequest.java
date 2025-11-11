package com.ecommerce.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    
    @NotNull(message = "产品变体ID不能为空")
    private Long variantId;
    
    @NotNull(message = "当前库存不能为空")
    @PositiveOrZero(message = "当前库存必须大于等于0")
    private Integer currentStock;
    
    @Builder.Default
    private Integer minimumStockLevel = 5;
    
    @Builder.Default
    private Integer maximumStockLevel = 1000;
    
    @Builder.Default
    private Integer reorderPoint = 10;
    
    @Builder.Default
    private Boolean isTracked = true;
    
    private String notes;
}