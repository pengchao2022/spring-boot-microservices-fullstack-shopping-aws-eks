package com.ecommerce.order.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String imageUrl;
    private BigDecimal weight;
    private Boolean isDigital;
    private LocalDateTime createdAt;
}
