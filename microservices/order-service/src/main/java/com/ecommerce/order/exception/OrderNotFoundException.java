package com.ecommerce.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(Long orderId) {
        super("Order not found with id: " + orderId);
    }
    
    public OrderNotFoundException(String orderNumber, boolean isNumber) {
        super("Order not found: " + orderNumber);
    }
}
