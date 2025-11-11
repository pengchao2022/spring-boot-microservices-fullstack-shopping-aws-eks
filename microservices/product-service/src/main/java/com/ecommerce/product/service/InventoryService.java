package com.ecommerce.product.service;

import com.ecommerce.product.exception.InsufficientStockException;
import com.ecommerce.product.model.entity.Inventory;
import com.ecommerce.product.model.entity.ProductVariant;
import com.ecommerce.product.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;

    public Inventory getInventoryByVariantId(Long variantId) {
        return inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for variant: " + variantId));
    }

    @Transactional
    public void reserveStock(Long variantId, int quantity) {
        Inventory inventory = getInventoryByVariantId(variantId);
        
        if (!inventory.reserveStock(quantity)) {
            throw new InsufficientStockException(
                    "Insufficient stock for variant: " + variantId + 
                    ". Available: " + inventory.getAvailableStock() + 
                    ", Requested: " + quantity
            );
        }
        
        inventoryRepository.save(inventory);
        log.info("Reserved {} units for variant: {}", quantity, variantId);
    }

    @Transactional
    public void releaseStock(Long variantId, int quantity) {
        Inventory inventory = getInventoryByVariantId(variantId);
        inventory.releaseStock(quantity);
        inventoryRepository.save(inventory);
        log.info("Released {} units for variant: {}", quantity, variantId);
    }

    @Transactional
    public void sellStock(Long variantId, int quantity) {
        Inventory inventory = getInventoryByVariantId(variantId);
        
        if (inventory.getAvailableStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for sale. Available: " + 
                    inventory.getAvailableStock() + ", Requested: " + quantity
            );
        }
        
        inventory.sellStock(quantity);
        inventoryRepository.save(inventory);
        log.info("Sold {} units for variant: {}", quantity, variantId);
    }

    @Transactional
    public void restock(Long variantId, int quantity) {
        Inventory inventory = getInventoryByVariantId(variantId);
        inventory.restock(quantity);
        inventoryRepository.save(inventory);
        log.info("Restocked {} units for variant: {}", quantity, variantId);
    }
}
