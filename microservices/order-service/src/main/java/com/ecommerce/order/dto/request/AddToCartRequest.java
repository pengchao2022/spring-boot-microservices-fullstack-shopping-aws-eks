package com.ecommerce.order.dto.request;

import java.math.BigDecimal;

public class AddToCartRequest {
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private String weight;
    
    // Constructors
    public AddToCartRequest() {}
    
    public AddToCartRequest(Long productId, String productName, String imageUrl, BigDecimal price, Integer quantity, String weight) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.weight = weight;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
}