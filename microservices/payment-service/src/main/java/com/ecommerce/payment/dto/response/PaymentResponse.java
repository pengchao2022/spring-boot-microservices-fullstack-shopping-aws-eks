package com.ecommerce.payment.dto.response;

import com.ecommerce.payment.model.enums.PaymentMethod;
import com.ecommerce.payment.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String alipayTradeNo;
    private String subject;
    private String body;
    private String currency;
    private String payerUserId;
    private String payerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private LocalDateTime cancelledAt;
    private String failureReason;
    private String paymentUrl; // 支付宝支付页面URL
}
