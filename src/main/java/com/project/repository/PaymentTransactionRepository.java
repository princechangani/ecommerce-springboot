package com.project.repository;

import com.project.entity.PaymentTransaction;
import com.project.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByOrderId(Long orderId);
    
    List<PaymentTransaction> findByStatus(PaymentStatus status);
    
    Optional<PaymentTransaction> findByTransactionId(String transactionId);
    
    List<PaymentTransaction> findByStatusOrderByCreatedAtDesc(PaymentStatus status);
}
