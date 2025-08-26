package com.project.controller;

import com.project.entity.ProductReview;
import com.project.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Review Management", description = "APIs for managing product reviews")
@SecurityRequirement(name = "bearerAuth")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product reviews", description = "Retrieve all reviews for a specific product")
    public ResponseEntity<List<ProductReview>> getProductReviews(@PathVariable Long productId) {
        List<ProductReview> reviews = productReviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review by ID", description = "Retrieve a specific review by its ID")
    public ResponseEntity<ProductReview> getReviewById(@PathVariable Long reviewId) {
        ProductReview review = productReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/product/{productId}")
    @Operation(summary = "Create product review", description = "Create a new review for a product")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductReview> createReview(
            @RequestParam Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductReview review) {
        ProductReview createdReview = productReviewService.createReview(userId, productId, review);
        return ResponseEntity.ok(createdReview);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update review", description = "Update an existing review")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductReview> updateReview(
            @RequestParam Long userId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ProductReview reviewDetails) {
        ProductReview updatedReview = productReviewService.updateReview(userId, reviewId, reviewDetails);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete review", description = "Delete a review")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReview(
            @RequestParam Long userId,
            @PathVariable Long reviewId) {
        productReviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user reviews", description = "Retrieve all reviews by a specific user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductReview>> getUserReviews(@PathVariable Long userId) {
        List<ProductReview> reviews = productReviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/rating/{rating}")
    @Operation(summary = "Get reviews by rating", description = "Retrieve all reviews for a product with a specific rating")
    public ResponseEntity<List<ProductReview>> getReviewsByRating(
            @PathVariable Long productId,
            @PathVariable Integer rating) {
        // This functionality can be implemented by filtering the product reviews on the frontend
        // or by adding the method to the service layer
        List<ProductReview> allReviews = productReviewService.getProductReviews(productId);
        List<ProductReview> filteredReviews = allReviews.stream()
                .filter(review -> review.getRating().equals(rating))
                .toList();
        return ResponseEntity.ok(filteredReviews);
    }

    @GetMapping("/product/{productId}/average-rating")
    @Operation(summary = "Get average product rating", description = "Get the average rating for a specific product")
    public ResponseEntity<Double> getAverageProductRating(@PathVariable Long productId) {
        double avgRating = productReviewService.getAverageProductRating(productId);
        return ResponseEntity.ok(avgRating);
    }

    @GetMapping("/product/{productId}/count")
    @Operation(summary = "Get review count", description = "Get the total number of reviews for a product")
    public ResponseEntity<Long> getProductReviewCount(@PathVariable Long productId) {
        long count = productReviewService.getProductReviewCount(productId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/product/{productId}/check-user-review")
    @Operation(summary = "Check if user reviewed product", description = "Check if a user has reviewed a specific product")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> hasUserReviewedProduct(
            @RequestParam Long userId,
            @PathVariable Long productId) {
        boolean hasReviewed = productReviewService.hasUserReviewedProduct(userId, productId);
        return ResponseEntity.ok(hasReviewed);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent reviews", description = "Get the most recent reviews")
    public ResponseEntity<List<ProductReview>> getRecentReviews() {
        List<ProductReview> reviews = productReviewService.getRecentReviews();
        return ResponseEntity.ok(reviews);
    }
}
