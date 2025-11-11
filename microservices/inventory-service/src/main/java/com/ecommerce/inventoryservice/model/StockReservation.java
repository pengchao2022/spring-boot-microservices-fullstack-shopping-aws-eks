package com.ecommerce.inventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_reservation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reservation_id", nullable = false, unique = true)
    private String reservationId;
    
    @Column(name = "variant_id", nullable = false)
    private Long variantId;
    
    @Column(name = "order_id", nullable = false)
    private String orderId;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        EXPIRED
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}