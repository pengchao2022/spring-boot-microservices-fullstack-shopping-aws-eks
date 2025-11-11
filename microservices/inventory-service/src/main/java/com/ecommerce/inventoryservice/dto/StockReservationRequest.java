package com.ecommerce.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationRequest {
    
    @NotBlank(message = "订单ID不能为空")
    private String orderId;
    
    @NotNull(message = "产品变体ID不能为空")
    private Long variantId;
    
    @NotNull(message = "预留数量不能为空")
    @Positive(message = "预留数量必须大于0")
    private Integer quantity;
    
    @Builder.Default
    private Integer expirationMinutes = 30;
}