package com.ecommerce.payment.service;

import java.math.BigDecimal;
import java.util.Map;

public interface AlipayService {
    
    String createPayment(String orderNumber, BigDecimal amount, String subject);
    
    boolean verifyPayment(Map<String, String> params);
    
    Map<String, String> queryPaymentStatus(String orderNumber);
    
    String createRefund(String alipayTradeNo, BigDecimal amount, String reason);
}