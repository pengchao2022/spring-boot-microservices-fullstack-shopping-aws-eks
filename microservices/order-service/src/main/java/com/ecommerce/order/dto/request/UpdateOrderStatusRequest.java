package com.ecommerce.order.dto.request;

import com.ecommerce.order.model.enums.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "Status is required")
    private OrderStatus status;
    
    private String notes;
    
    private String trackingNumber;
}
