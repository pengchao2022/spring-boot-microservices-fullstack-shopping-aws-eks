package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "inventory-service", url = "${app.inventory-service.url}")
public interface InventoryClient {
    
    @PostMapping("/api/inventory/reserve")
    Map<String, Object> reserveStock(@RequestBody Map<String, Object> request);
    
    @PostMapping("/api/inventory/release")
    Map<String, Object> releaseStock(@RequestBody Map<String, Object> request);
}
