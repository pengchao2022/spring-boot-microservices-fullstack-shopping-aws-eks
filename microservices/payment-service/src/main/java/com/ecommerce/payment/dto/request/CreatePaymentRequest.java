package com.ecommerce.payment.dto.request;

import com.ecommerce.payment.model.enums.PaymentMethod;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {
    @NotBlank(message = "Order number is required")
    private String orderNumber;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    private String body;
    
    private String currency = "CNY";
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}