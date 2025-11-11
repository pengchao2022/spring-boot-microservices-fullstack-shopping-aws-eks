package com.ecommerce.product.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class HomeProductsDTO {
    private List<ProductResponse> featuredFruits;
    private List<ProductResponse> featuredVegetables;
    private List<ProductResponse> hotSales;
    private List<ProductResponse> newArrivals;
}
