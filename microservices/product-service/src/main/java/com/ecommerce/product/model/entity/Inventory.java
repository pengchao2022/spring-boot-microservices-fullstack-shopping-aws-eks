package com.ecommerce.product.model.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false, unique = true)
    private ProductVariant variant;
    
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;
    
    @Column(name = "reserved_stock")
    private Integer reservedStock = 0;
    
    @Column(name = "available_stock")
    private Integer availableStock = 0;
    
    @Column(name = "total_sold")
    private Integer totalSold = 0;
    
    @Column(name = "last_restocked_date")
    private LocalDateTime lastRestockedDate;
    
    @Column(name = "last_sold_date")
    private LocalDateTime lastSoldDate;
    
    @Enumerated(EnumType.STRING)
    private InventoryStatus status = InventoryStatus.IN_STOCK;
    
    // Getter and Setter methods
    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Integer totalSold) {
        this.totalSold = totalSold;
    }

    public LocalDateTime getLastRestockedDate() {
        return lastRestockedDate;
    }

    public void setLastRestockedDate(LocalDateTime lastRestockedDate) {
        this.lastRestockedDate = lastRestockedDate;
    }

    public LocalDateTime getLastSoldDate() {
        return lastSoldDate;
    }

    public void setLastSoldDate(LocalDateTime lastSoldDate) {
        this.lastSoldDate = lastSoldDate;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    // Business methods
    public boolean reserveStock(int quantity) {
        if (availableStock >= quantity) {
            reservedStock += quantity;
            availableStock -= quantity;
            return true;
        }
        return false;
    }

    public void releaseStock(int quantity) {
        reservedStock = Math.max(0, reservedStock - quantity);
        availableStock += quantity;
    }

    public void sellStock(int quantity) {
        currentStock = Math.max(0, currentStock - quantity);
        reservedStock = Math.max(0, reservedStock - quantity);
        totalSold += quantity;
        lastSoldDate = LocalDateTime.now();
    }

    public void restock(int quantity) {
        currentStock += quantity;
        availableStock += quantity;
        lastRestockedDate = LocalDateTime.now();
        
        if (currentStock > 0) {
            status = InventoryStatus.IN_STOCK;
        }
    }
}