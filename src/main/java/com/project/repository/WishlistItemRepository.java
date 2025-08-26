package com.project.repository;

import com.project.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUserId(Long userId);
    
    List<WishlistItem> findByProductId(Long productId);
    
    Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    @Modifying
    int deleteByUserIdAndProductId(Long userId, Long productId);
    
    long countByUserId(Long userId);
}
