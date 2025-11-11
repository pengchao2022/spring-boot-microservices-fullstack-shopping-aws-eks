package com.ecommerce.order.dto.response;

import com.ecommerce.order.model.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusHistoryResponse {
    private Long id;
    private OrderStatus status;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
}
