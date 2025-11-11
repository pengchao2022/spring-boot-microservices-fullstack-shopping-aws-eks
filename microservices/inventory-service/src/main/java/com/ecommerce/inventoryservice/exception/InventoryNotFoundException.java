package com.ecommerce.inventoryservice.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
    
    public InventoryNotFoundException(Long variantId) {
        super(String.format("Inventory not found for variant ID: %d", variantId));
    }
}
