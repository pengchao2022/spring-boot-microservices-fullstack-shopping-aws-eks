package com.ecommerce.payment.model;

import com.ecommerce.payment.model.enums.PaymentMethod;
import com.ecommerce.payment.model.enums.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.ALIPAY;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "alipay_trade_no")
    private String alipayTradeNo;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "body")
    private String body;
    
    @Column(name = "currency")
    private String currency = "CNY";
    
    @Column(name = "payer_user_id")
    private String payerUserId;
    
    @Column(name = "payer_email")
    private String payerEmail;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Version
    private Integer version;
    
    @PrePersist
    protected void onCreate() {
        if (paymentMethod == null) {
            paymentMethod = PaymentMethod.ALIPAY;
        }
        if (currency == null) {
            currency = "CNY";
        }
    }
    
    public void markAsPaid(String alipayTradeNo, String payerUserId, String payerEmail) {
        this.status = PaymentStatus.PAID;
        this.alipayTradeNo = alipayTradeNo;
        this.payerUserId = payerUserId;
        this.payerEmail = payerEmail;
        this.paidAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }
    
    public void markAsCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
    
    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = LocalDateTime.now();
    }
    
    public boolean canBeRefunded() {
        return status == PaymentStatus.PAID;
    }
    
    public boolean canBeCancelled() {
        return status == PaymentStatus.PENDING;
    }
}
