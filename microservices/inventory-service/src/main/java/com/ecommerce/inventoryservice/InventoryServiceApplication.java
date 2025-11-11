package com.ecommerce.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

// 添加明确的包扫描配置
@SpringBootApplication(scanBasePackages = "com.ecommerce.inventoryservice")
@ComponentScan(basePackages = "com.ecommerce.inventoryservice")
@EnableScheduling
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}