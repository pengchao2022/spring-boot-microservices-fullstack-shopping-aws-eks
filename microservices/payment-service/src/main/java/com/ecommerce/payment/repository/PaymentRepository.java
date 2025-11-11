package com.ecommerce.payment.repository;

import com.ecommerce.payment.model.Payment;
import com.ecommerce.payment.model.enums.PaymentMethod;
import com.ecommerce.payment.model.enums.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByOrderNumber(String orderNumber);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    Optional<Payment> findByAlipayTradeNo(String alipayTradeNo);
    
    Optional<Payment> findByIdAndOrderNumber(Long id, String orderNumber);
    
    List<Payment> findByPayerUserId(String payerUserId);
    
    // 使用 Pageable 进行灵活的分页查询
    List<Payment> findByOrderByCreatedAtDesc(Pageable pageable);
    
    // 统计方法
    long countByPaymentMethod(PaymentMethod paymentMethod);
    
    List<Payment> findByCreatedAtAfter(LocalDateTime dateTime);
    
    // 其他有用的查询方法
    List<Payment> findByPaymentMethodAndStatus(PaymentMethod paymentMethod, PaymentStatus status);
    
    Optional<Payment> findByOrderNumberAndStatus(String orderNumber, PaymentStatus status);
    
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    long countByStatus(PaymentStatus status);
}