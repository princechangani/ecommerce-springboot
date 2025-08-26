package com.project.repository;

import com.project.entity.InventoryTransaction;
import com.project.enums.InventoryTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    List<InventoryTransaction> findByTypeOrderByCreatedAtDesc(InventoryTransactionType type);
    
    List<InventoryTransaction> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COALESCE(SUM(it.quantityChange), 0) FROM InventoryTransaction it WHERE it.product.id = :productId")
    Integer getCurrentStock(@Param("productId") Long productId);
}
