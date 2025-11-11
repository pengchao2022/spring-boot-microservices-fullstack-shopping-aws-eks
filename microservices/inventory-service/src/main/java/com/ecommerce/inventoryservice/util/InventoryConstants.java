package com.ecommerce.inventoryservice.util;

public class InventoryConstants {
    
    private InventoryConstants() {
        // 工具类，防止实例化
    }
    
    // 默认库存配置
    public static final Integer DEFAULT_MINIMUM_STOCK_LEVEL = 5;
    public static final Integer DEFAULT_MAXIMUM_STOCK_LEVEL = 1000;
    public static final Integer DEFAULT_REORDER_POINT = 10;
    public static final Integer DEFAULT_RESERVATION_EXPIRATION_MINUTES = 30;
    
    // 库存状态
    public static final String STATUS_IN_STOCK = "IN_STOCK";
    public static final String STATUS_LOW_STOCK = "LOW_STOCK";
    public static final String STATUS_OUT_OF_STOCK = "OUT_OF_STOCK";
    public static final String STATUS_DISCONTINUED = "DISCONTINUED";
    
    // 预留状态
    public static final String RESERVATION_PENDING = "PENDING";
    public static final String RESERVATION_CONFIRMED = "CONFIRMED";
    public static final String RESERVATION_CANCELLED = "CANCELLED";
    public static final String RESERVATION_EXPIRED = "EXPIRED";
    
    // API 路径
    public static final String API_BASE_PATH = "/api/inventory";
    public static final String API_RESERVATIONS_PATH = "/api/inventory/reservations";
    
    // 错误消息
    public static final String ERROR_INVENTORY_NOT_FOUND = "Inventory not found for variant ID: %d";
    public static final String ERROR_INSUFFICIENT_STOCK = "Insufficient stock for variant %d. Requested: %d, Available: %d";
    public static final String ERROR_RESERVATION_NOT_FOUND = "Reservation not found: %s";
    public static final String ERROR_RESERVATION_EXPIRED = "Reservation has expired: %s";
}
