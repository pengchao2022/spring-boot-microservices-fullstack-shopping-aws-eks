package com.ecommerce.order.dto.response;

import java.util.List;

public class CartResponse {
    private Long id;
    private Long userId;
    private List<CartItemResponse> items;
    private Integer totalItems;
    
    // Constructors
    public CartResponse() {}
    
    public CartResponse(Long id, Long userId, List<CartItemResponse> items, Integer totalItems) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.totalItems = totalItems;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }
    
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}