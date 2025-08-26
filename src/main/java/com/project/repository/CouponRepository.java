package com.project.repository;

import com.project.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);
    
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND " +
           "(c.startsAt IS NULL OR c.startsAt <= :now) AND " +
           "(c.expiresAt IS NULL OR c.expiresAt >= :now)")
    List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.isActive = true AND " +
           "(c.startsAt IS NULL OR c.startsAt <= :now) AND " +
           "(c.expiresAt IS NULL OR c.expiresAt >= :now) AND " +
           "(c.usageLimit IS NULL OR c.usedCount < c.usageLimit)")
    Optional<Coupon> findValidCoupon(@Param("code") String code, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount + 1 WHERE c.id = :couponId")
    void incrementUsage(@Param("couponId") Long couponId);
}
