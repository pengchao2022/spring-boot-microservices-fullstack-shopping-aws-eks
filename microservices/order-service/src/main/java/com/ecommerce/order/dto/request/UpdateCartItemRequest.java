package com.ecommerce.order.dto.request;

public class UpdateCartItemRequest {
    private Integer quantity;
    
    // Constructors
    public UpdateCartItemRequest() {}
    
    public UpdateCartItemRequest(Integer quantity) {
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}