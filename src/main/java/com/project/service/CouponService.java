package com.project.service;

import com.project.entity.Coupon;
import com.project.entity.Order;
import com.project.enums.DiscountType;
import com.project.exception.ResourceNotFoundException;
import com.project.exception.ResourceAlreadyExists;
import com.project.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
    }

    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));
    }

    public Coupon createCoupon(Coupon coupon) {
        // Check if coupon with same code already exists
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new ResourceAlreadyExists("Coupon with code '" + coupon.getCode() + "' already exists");
        }

        // Set default values
        if (coupon.getIsActive() == null) {
            coupon.setIsActive(true);
        }
        if (coupon.getUsageLimit() == null) {
            coupon.setUsageLimit(0); // Unlimited usage
        }
        if (coupon.getUsedCount() == null) {
            coupon.setUsedCount(0);
        }

        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Long id, Coupon couponDetails) {
        Coupon coupon = getCouponById(id);

        // Check if new code conflicts with existing coupon (excluding current one)
        if (!coupon.getCode().equals(couponDetails.getCode()) && 
            couponRepository.findByCode(couponDetails.getCode()).isPresent()) {
            throw new ResourceAlreadyExists("Coupon with code '" + couponDetails.getCode() + "' already exists");
        }

        if (couponDetails.getCode() != null) {
            coupon.setCode(couponDetails.getCode());
        }
        if (couponDetails.getType() != null) {
            coupon.setType(couponDetails.getType());
        }
        if (couponDetails.getValue() != null) {
            coupon.setValue(couponDetails.getValue());
        }
        if (couponDetails.getMinimumAmount() != null) {
            coupon.setMinimumAmount(couponDetails.getMinimumAmount());
        }
        if (couponDetails.getMaximumDiscount() != null) {
            coupon.setMaximumDiscount(couponDetails.getMaximumDiscount());
        }
        if (couponDetails.getStartsAt() != null) {
            coupon.setStartsAt(couponDetails.getStartsAt());
        }
        if (couponDetails.getExpiresAt() != null) {
            coupon.setExpiresAt(couponDetails.getExpiresAt());
        }
        if (couponDetails.getUsageLimit() != null) {
            coupon.setUsageLimit(couponDetails.getUsageLimit());
        }
        if (couponDetails.getIsActive() != null) {
            coupon.setIsActive(couponDetails.getIsActive());
        }

        return couponRepository.save(coupon);
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponRepository.delete(coupon);
    }

    public void deactivateCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    public void activateCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        coupon.setIsActive(true);
        couponRepository.save(coupon);
    }

    public List<Coupon> getActiveCoupons() {
        return couponRepository.findAll().stream()
                .filter(Coupon::getIsActive)
                .toList();
    }

    public List<Coupon> getValidCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findActiveCoupons(now);
    }

    public boolean isCouponValid(String code) {
        try {
            Coupon coupon = getCouponByCode(code);
            return validateCoupon(coupon);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public boolean validateCoupon(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check if coupon is active
        if (!coupon.getIsActive()) {
            return false;
        }
        
        // Check validity period
        if (coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt())) {
            return false;
        }
        if (coupon.getExpiresAt() != null && now.isAfter(coupon.getExpiresAt())) {
            return false;
        }
        
        // Check usage limit
        if (coupon.getUsageLimit() > 0 && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return false;
        }
        
        return true;
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        if (!validateCoupon(coupon)) {
            return BigDecimal.ZERO;
        }
        
        // Check minimum order amount
        if (coupon.getMinimumAmount() != null && orderAmount.compareTo(coupon.getMinimumAmount()) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        if (coupon.getType() == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100));
        } else {
            discount = coupon.getValue();
        }
        
        // Apply maximum discount limit
        if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
            discount = coupon.getMaximumDiscount();
        }
        
        return discount;
    }

    public void applyCoupon(String code, Order order) {
        Coupon coupon = getCouponByCode(code);
        
        if (!validateCoupon(coupon)) {
            throw new IllegalStateException("Coupon is not valid");
        }
        
        BigDecimal discount = calculateDiscount(coupon, order.getTotalAmount());
        order.setDiscountAmount(discount);
        
        // Increment usage count
        couponRepository.incrementUsage(coupon.getId());
    }

    public List<Coupon> getExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findAll().stream()
                .filter(coupon -> coupon.getExpiresAt() != null && now.isAfter(coupon.getExpiresAt()))
                .toList();
    }

    public List<Coupon> getUpcomingCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findAll().stream()
                .filter(coupon -> coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt()))
                .toList();
    }
}
