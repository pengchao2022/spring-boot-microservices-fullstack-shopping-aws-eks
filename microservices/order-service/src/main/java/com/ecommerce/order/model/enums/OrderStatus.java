package com.ecommerce.order.model.enums;

public enum OrderStatus {
    PENDING,        // 待支付
    CONFIRMED,      // 已确认
    PROCESSING,     // 处理中
    SHIPPED,        // 已发货
    DELIVERED,      // 已送达
    CANCELLED,      // 已取消
    REFUNDED        // 已退款
}
