package com.ecommerce.payment.dto.response;

import com.ecommerce.payment.model.enums.RefundStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundResponse {
    private Long id;
    private String refundNumber;
    private Long paymentId;
    private String orderNumber;
    private BigDecimal amount;
    private RefundStatus status;
    private String alipayRefundNo;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime refundedAt;
    private String failureReason;
}