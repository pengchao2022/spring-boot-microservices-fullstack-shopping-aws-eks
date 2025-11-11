package com.ecommerce.inventoryservice.repository;

import com.ecommerce.inventoryservice.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    
    Optional<StockReservation> findByReservationId(String reservationId);
    
    List<StockReservation> findByOrderId(String orderId);
    
    List<StockReservation> findByVariantId(Long variantId);
    
    List<StockReservation> findByStatus(StockReservation.ReservationStatus status);
    
    List<StockReservation> findByExpiresAtBeforeAndStatus(LocalDateTime dateTime, StockReservation.ReservationStatus status);
    
    @Query("SELECT sr FROM StockReservation sr WHERE sr.orderId = :orderId AND sr.variantId = :variantId AND sr.status = 'PENDING'")
    List<StockReservation> findPendingReservationsByOrderAndVariant(@Param("orderId") String orderId, @Param("variantId") Long variantId);
    
    @Modifying
    @Query("UPDATE StockReservation sr SET sr.status = 'EXPIRED' WHERE sr.expiresAt < CURRENT_TIMESTAMP AND sr.status = 'PENDING'")
    int expireOldReservations();
}
