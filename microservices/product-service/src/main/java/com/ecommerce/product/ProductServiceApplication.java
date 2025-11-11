package com.ecommerce.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.ecommerce.product.controller",
    "com.ecommerce.product.service", 
    "com.ecommerce.product.repository",
    "com.ecommerce.product.model",
    "com.ecommerce.product.config"
})
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}