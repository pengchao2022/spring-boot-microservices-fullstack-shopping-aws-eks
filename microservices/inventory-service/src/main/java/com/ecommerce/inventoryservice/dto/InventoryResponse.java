package com.ecommerce.inventoryservice.dto;

import com.ecommerce.inventoryservice.model.Inventory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private Long variantId;
    private Integer currentStock;
    private Integer reservedStock;
    private Integer availableStock;
    private Integer minimumStockLevel;
    private Integer maximumStockLevel;
    private Integer reorderPoint;
    private Integer totalSold;
    private Integer totalReturned;
    private LocalDateTime lastRestockedDate;
    private LocalDateTime lastSoldDate;
    private Boolean isTracked;
    private Inventory.InventoryStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static InventoryResponse fromEntity(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .variantId(inventory.getVariantId())
                .currentStock(inventory.getCurrentStock())
                .reservedStock(inventory.getReservedStock())
                .availableStock(inventory.getAvailableStock())
                .minimumStockLevel(inventory.getMinimumStockLevel())
                .maximumStockLevel(inventory.getMaximumStockLevel())
                .reorderPoint(inventory.getReorderPoint())
                .totalSold(inventory.getTotalSold())
                .totalReturned(inventory.getTotalReturned())
                .lastRestockedDate(inventory.getLastRestockedDate())
                .lastSoldDate(inventory.getLastSoldDate())
                .isTracked(inventory.getIsTracked())
                .status(inventory.getStatus())
                .notes(inventory.getNotes())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
