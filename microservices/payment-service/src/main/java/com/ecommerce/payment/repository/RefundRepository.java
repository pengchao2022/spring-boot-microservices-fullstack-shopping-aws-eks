package com.ecommerce.payment.repository;

import com.ecommerce.payment.model.Refund;
import com.ecommerce.payment.model.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    List<Refund> findByPaymentId(Long paymentId);
    
    Optional<Refund> findByRefundNumber(String refundNumber);
    
    List<Refund> findByStatus(RefundStatus status);
}