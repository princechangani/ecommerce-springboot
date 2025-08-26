package com.project.repository;

import com.project.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductIdAndIsApprovedTrue(Long productId);
    
    List<ProductReview> findByUserId(Long userId);
    
    Page<ProductReview> findByProductIdAndIsApprovedTrue(Long productId, Pageable pageable);
    
    long countByProductIdAndIsApprovedTrue(Long productId);
    
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.id = :productId AND pr.isApproved = true")
    Double getAverageRating(@Param("productId") Long productId);
}
