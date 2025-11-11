package com.ecommerce.product.model.dto;

import java.util.List;

public class ProductDetailDTO {
    private ProductResponse product;
    private List<com.ecommerce.product.model.dto.ProductVariantResponse> variants;
    private com.ecommerce.product.model.dto.ProductVariantResponse defaultVariant;
    
    // Getter and Setter methods
    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public List<com.ecommerce.product.model.dto.ProductVariantResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<com.ecommerce.product.model.dto.ProductVariantResponse> variants) {
        this.variants = variants;
    }

    public com.ecommerce.product.model.dto.ProductVariantResponse getDefaultVariant() {
        return defaultVariant;
    }

    public void setDefaultVariant(com.ecommerce.product.model.dto.ProductVariantResponse defaultVariant) {
        this.defaultVariant = defaultVariant;
    }
}