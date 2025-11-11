package com.ecommerce.order.dto.request;

import com.ecommerce.order.model.Address;
import com.ecommerce.order.model.enums.PaymentMethod;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long userId;
    
    private String guestEmail;
    
    @NotNull(message = "Shipping address is required")
    private Address shippingAddress;
    
    private Address billingAddress;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod = PaymentMethod.ALIPAY;
    
    private String shippingMethod;
    
    private String notes;
    
    private String currency = "CNY";
    
    private BigDecimal shippingAmount = BigDecimal.ZERO;
    
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Valid
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;
}
