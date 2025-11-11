package com.ecommerce.order.dto.response;

import com.ecommerce.order.model.Address;
import com.ecommerce.order.model.enums.OrderStatus;
import com.ecommerce.order.model.enums.PaymentMethod;
import com.ecommerce.order.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String guestEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal subtotalAmount;
    private BigDecimal shippingAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String currency;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentId;
    private String shippingMethod;
    private String trackingNumber;
    private String notes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private List<OrderItemResponse> orderItems;
    private List<OrderStatusHistoryResponse> statusHistory;
    private boolean guestOrder;
}
