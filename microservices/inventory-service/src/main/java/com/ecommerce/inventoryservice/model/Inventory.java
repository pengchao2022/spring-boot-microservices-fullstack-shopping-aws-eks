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
@Table(name = "inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "variant_id", nullable = false, unique = true)
    private Long variantId;
    
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;
    
    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock = 0;
    
    @Column(name = "available_stock", nullable = false)
    private Integer availableStock = 0;
    
    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel = 5;
    
    @Column(name = "maximum_stock_level", nullable = false)
    private Integer maximumStockLevel = 1000;
    
    @Column(name = "reorder_point", nullable = false)
    private Integer reorderPoint = 10;
    
    @Column(name = "total_sold", nullable = false)
    private Integer totalSold = 0;
    
    @Column(name = "total_returned", nullable = false)
    private Integer totalReturned = 0;
    
    @Column(name = "last_restocked_date")
    private LocalDateTime lastRestockedDate;
    
    @Column(name = "last_sold_date")
    private LocalDateTime lastSoldDate;
    
    @Column(name = "is_tracked", nullable = false)
    private Boolean isTracked = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InventoryStatus status = InventoryStatus.IN_STOCK;
    
    @Column(name = "notes")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum InventoryStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK,
        DISCONTINUED
    }
    
    @PreUpdate
    @PrePersist
    public void calculateAvailableStock() {
        this.availableStock = Math.max(0, this.currentStock - this.reservedStock);
        updateStatus();
    }
    
    private void updateStatus() {
        if (!isTracked) {
            this.status = InventoryStatus.IN_STOCK;
            return;
        }
        
        if (currentStock <= 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        } else if (availableStock <= reorderPoint) {
            this.status = InventoryStatus.LOW_STOCK;
        } else {
            this.status = InventoryStatus.IN_STOCK;
        }
    }
}