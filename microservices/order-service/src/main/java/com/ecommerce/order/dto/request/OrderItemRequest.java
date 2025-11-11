package com.ecommerce.order.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OrderItemRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Product name is required")
    private String productName;
    
    private String sku;
    
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String imageUrl;
    
    private BigDecimal weight;
    
    private Boolean isDigital = false;
}
