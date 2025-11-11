package com.ecommerce.payment.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RefundRequest {
    @NotBlank(message = "Order number is required")
    private String orderNumber;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotBlank(message = "Reason is required")
    private String reason;
}
