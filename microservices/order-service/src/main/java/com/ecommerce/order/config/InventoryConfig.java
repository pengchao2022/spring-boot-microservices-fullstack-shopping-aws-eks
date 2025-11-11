package com.ecommerce.order.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.inventory")
@Getter
@Setter
public class InventoryConfig {
    private boolean checkEnabled = true;
    private boolean reservationEnabled = true;
    private boolean validationEnabled = true;
    private String baseUrl = "http://inventory-service:8080";
    private int timeoutMs = 5000;
    private boolean fallbackEnabled = false;
    
    public boolean isInventoryOperationsEnabled() {
        return checkEnabled && reservationEnabled && validationEnabled;
    }
    
    public boolean isReservationAllowed() {
        return checkEnabled && reservationEnabled;
    }
}