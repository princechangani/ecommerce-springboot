package com.project.service;

import com.project.entity.Product;
import com.project.entity.ProductReview;
import com.project.entity.User;
import com.project.exception.ResourceNotFoundException;
import com.project.exception.ResourceAlreadyExists;
import com.project.repository.ProductRepository;
import com.project.repository.ProductReviewRepository;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<ProductReview> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return productReviewRepository.findByProductIdAndIsApprovedTrue(productId);
    }

    public ProductReview getReviewById(Long reviewId) {
        return productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Product review", "id", reviewId));
    }

    public ProductReview createReview(Long userId, Long productId, ProductReview review) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Check if user has already reviewed this product
        List<ProductReview> existingReviews = productReviewRepository.findByUserId(userId);
        boolean hasReviewed = existingReviews.stream()
                .anyMatch(r -> r.getProduct().getId().equals(productId));
        
        if (hasReviewed) {
            throw new ResourceAlreadyExists("User has already reviewed this product");
        }

        review.setUser(user);
        review.setProduct(product);
        // createdAt is automatically set by @CreationTimestamp

        ProductReview savedReview = productReviewRepository.save(review);
        
        return savedReview;
    }

    public ProductReview updateReview(Long userId, Long reviewId, ProductReview reviewDetails) {
        ProductReview review = getReviewById(reviewId);
        
        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Product review", "id", reviewId);
        }

        if (reviewDetails.getRating() != null) {
            review.setRating(reviewDetails.getRating());
        }
        if (reviewDetails.getTitle() != null) {
            review.setTitle(reviewDetails.getTitle());
        }
        if (reviewDetails.getComment() != null) {
            review.setComment(reviewDetails.getComment());
        }

        return productReviewRepository.save(review);
    }

    public void deleteReview(Long userId, Long reviewId) {
        ProductReview review = getReviewById(reviewId);
        
        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Product review", "id", reviewId);
        }

        productReviewRepository.delete(review);
    }

    public List<ProductReview> getUserReviews(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return productReviewRepository.findByUserId(userId);
    }

    public double getAverageProductRating(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        
        Double avgRating = productReviewRepository.getAverageRating(productId);
        return avgRating != null ? avgRating : 0.0;
    }

    public long getProductReviewCount(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return productReviewRepository.countByProductIdAndIsApprovedTrue(productId);
    }

    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        List<ProductReview> userReviews = productReviewRepository.findByUserId(userId);
        return userReviews.stream()
                .anyMatch(review -> review.getProduct().getId().equals(productId));
    }

    public List<ProductReview> getRecentReviews() {
        return productReviewRepository.findAll().stream()
                .limit(10)
                .toList();
    }
}
