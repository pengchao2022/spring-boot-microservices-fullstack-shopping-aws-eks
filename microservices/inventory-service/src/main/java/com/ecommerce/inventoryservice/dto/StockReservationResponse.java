package com.ecommerce.inventoryservice.dto;

import com.ecommerce.inventoryservice.model.StockReservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResponse {
    private Long id;
    private String reservationId;
    private Long variantId;
    private String orderId;
    private Integer quantity;
    private StockReservation.ReservationStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static StockReservationResponse fromEntity(StockReservation reservation) {
        return StockReservationResponse.builder()
                .id(reservation.getId())
                .reservationId(reservation.getReservationId())
                .variantId(reservation.getVariantId())
                .orderId(reservation.getOrderId())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus())
                .expiresAt(reservation.getExpiresAt())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
