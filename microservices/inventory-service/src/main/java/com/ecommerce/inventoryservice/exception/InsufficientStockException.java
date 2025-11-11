package com.ecommerce.inventoryservice.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(Long variantId, Integer requested, Integer available) {
        super(String.format("Insufficient stock for variant %d. Requested: %d, Available: %d", 
                           variantId, requested, available));
    }
}
