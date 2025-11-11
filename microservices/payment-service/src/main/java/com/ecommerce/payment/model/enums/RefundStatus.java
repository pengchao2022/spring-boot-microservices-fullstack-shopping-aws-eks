package com.ecommerce.payment.model.enums;

public enum RefundStatus {
    PENDING,    // 待处理
    SUCCESS,    // 退款成功
    FAILED,     // 退款失败
    CANCELLED   // 退款取消
}