package com.project.controller;

import com.project.entity.Coupon;
import com.project.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coupon Management", description = "APIs for managing coupons and discounts")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "Get all coupons", description = "Retrieve all available coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active coupons", description = "Retrieve all active coupons")
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        List<Coupon> coupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/valid")
    @Operation(summary = "Get valid coupons", description = "Retrieve all currently valid coupons")
    public ResponseEntity<List<Coupon>> getValidCoupons() {
        List<Coupon> coupons = couponService.getValidCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get coupon by ID", description = "Retrieve a specific coupon by its ID")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        Coupon coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(coupon);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get coupon by code", description = "Retrieve a coupon by its code")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String code) {
        Coupon coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(coupon);
    }

    @PostMapping
    @Operation(summary = "Create new coupon", description = "Create a new coupon (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update coupon", description = "Update an existing coupon (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @Valid @RequestBody Coupon couponDetails) {
        Coupon updatedCoupon = couponService.updateCoupon(id, couponDetails);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete coupon", description = "Delete a coupon (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate coupon", description = "Activate a coupon (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateCoupon(@PathVariable Long id) {
        couponService.activateCoupon(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate coupon", description = "Deactivate a coupon (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateCoupon(@PathVariable Long id) {
        couponService.deactivateCoupon(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate/{code}")
    @Operation(summary = "Validate coupon", description = "Check if a coupon code is valid")
    public ResponseEntity<Boolean> validateCoupon(@PathVariable String code) {
        boolean isValid = couponService.isCouponValid(code);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/calculate-discount")
    @Operation(summary = "Calculate discount", description = "Calculate discount amount for a coupon and order amount")
    public ResponseEntity<BigDecimal> calculateDiscount(@RequestParam String code, @RequestParam BigDecimal orderAmount) {
        Coupon coupon = couponService.getCouponByCode(code);
        BigDecimal discount = couponService.calculateDiscount(coupon, orderAmount);
        return ResponseEntity.ok(discount);
    }

    @GetMapping("/expired")
    @Operation(summary = "Get expired coupons", description = "Retrieve all expired coupons (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getExpiredCoupons() {
        List<Coupon> coupons = couponService.getExpiredCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming coupons", description = "Retrieve all upcoming coupons (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Coupon>> getUpcomingCoupons() {
        List<Coupon> coupons = couponService.getUpcomingCoupons();
        return ResponseEntity.ok(coupons);
    }
}
