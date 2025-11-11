package com.ecommerce.order.model;

import com.ecommerce.order.model.enums.OrderStatus;
import com.ecommerce.order.model.enums.PaymentMethod;
import com.ecommerce.order.model.enums.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "guest_email")
    private String guestEmail;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "subtotal_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalAmount;
    
    @Column(name = "shipping_amount", precision = 10, scale = 2)
    private BigDecimal shippingAmount = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "currency", nullable = false)
    private String currency = "CNY";
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "shipping_name")),
        @AttributeOverride(name = "phone", column = @Column(name = "shipping_phone")),
        @AttributeOverride(name = "province", column = @Column(name = "shipping_province")),
        @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
        @AttributeOverride(name = "district", column = @Column(name = "shipping_district")),
        @AttributeOverride(name = "detailAddress", column = @Column(name = "shipping_detail_address"))
    })
    private Address shippingAddress;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "billing_name")),
        @AttributeOverride(name = "phone", column = @Column(name = "billing_phone")),
        @AttributeOverride(name = "province", column = @Column(name = "billing_province")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "district", column = @Column(name = "billing_district")),
        @AttributeOverride(name = "detailAddress", column = @Column(name = "billing_detail_address"))
    })
    private Address billingAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod = PaymentMethod.ALIPAY;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_id")
    private String paymentId;
    
    @Column(name = "shipping_method")
    private String shippingMethod;
    
    @Column(name = "tracking_number")
    private String trackingNumber;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();
    
    @Version
    private Integer version;
    
    @PrePersist
    protected void onCreate() {
        if (orderNumber == null) {
            orderNumber = "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        }
    }
    
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
    
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }
    
    public void ship(String trackingNumber) {
        this.status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.shippedAt = LocalDateTime.now();
    }
    
    public void deliver() {
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }
    
    public void cancel(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.CANCELLED;
    }
    
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    public BigDecimal calculateTotalAmount() {
        BigDecimal total = subtotalAmount
            .add(taxAmount != null ? taxAmount : BigDecimal.ZERO)
            .add(shippingAmount != null ? shippingAmount : BigDecimal.ZERO)
            .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        return total.compareTo(BigDecimal.ZERO) >= 0 ? total : BigDecimal.ZERO;
    }
    
    public boolean isGuestOrder() {
        return userId == null && guestEmail != null;
    }
}