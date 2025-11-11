package com.ecommerce.payment.model;

import com.ecommerce.payment.model.enums.RefundStatus;
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
@Table(name = "refunds")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    
    @Column(name = "refund_number", unique = true, nullable = false)
    private String refundNumber;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status = RefundStatus.PENDING;
    
    @Column(name = "alipay_refund_no")
    private String alipayRefundNo;
    
    @Column(name = "reason")
    private String reason;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @PrePersist
    protected void onCreate() {
        if (refundNumber == null) {
            refundNumber = "REF" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        }
    }
    
    public void markAsSuccess(String alipayRefundNo) {
        this.status = RefundStatus.SUCCESS;
        this.alipayRefundNo = alipayRefundNo;
        this.refundedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = RefundStatus.FAILED;
        this.failureReason = reason;
    }
    
    // 添加 getProcessedAt 方法，返回 refundedAt
    public LocalDateTime getProcessedAt() {
        return refundedAt;
    }
}