package com.ecommerce.inventoryservice.repository;

import com.ecommerce.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByVariantId(Long variantId);
    
    List<Inventory> findByStatus(Inventory.InventoryStatus status);
    
    List<Inventory> findByIsTrackedTrue();
    
    @Query("SELECT i FROM Inventory i WHERE i.availableStock <= i.reorderPoint AND i.isTracked = true")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT i FROM Inventory i WHERE i.availableStock = 0 AND i.isTracked = true")
    List<Inventory> findOutOfStockItems();
    
    @Modifying
    @Query("UPDATE Inventory i SET i.currentStock = i.currentStock + :quantity, i.lastRestockedDate = CURRENT_TIMESTAMP WHERE i.variantId = :variantId")
    void increaseStock(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Inventory i SET i.currentStock = i.currentStock - :quantity, i.lastSoldDate = CURRENT_TIMESTAMP WHERE i.variantId = :variantId AND i.availableStock >= :quantity")
    int decreaseStock(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Inventory i SET i.reservedStock = i.reservedStock + :quantity WHERE i.variantId = :variantId AND i.availableStock >= :quantity")
    int reserveStock(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Inventory i SET i.reservedStock = i.reservedStock - :quantity WHERE i.variantId = :variantId AND i.reservedStock >= :quantity")
    int releaseReservedStock(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Inventory i SET i.currentStock = i.currentStock - :quantity, i.reservedStock = i.reservedStock - :quantity, i.totalSold = i.totalSold + :quantity WHERE i.variantId = :variantId AND i.reservedStock >= :quantity")
    int confirmReservation(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
}
