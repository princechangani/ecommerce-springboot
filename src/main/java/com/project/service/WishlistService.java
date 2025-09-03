package com.project.service;

import com.project.entity.Product;
import com.project.entity.User;
import com.project.entity.WishlistItem;
import com.project.exception.ResourceNotFoundException;
import com.project.exception.ResourceAlreadyExists;
import com.project.repository.ProductRepository;
import com.project.repository.UserRepository;
import com.project.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<WishlistItem> getUserWishlist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return wishlistItemRepository.findByUserId(userId);
    }

    public WishlistItem addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Check if item already exists in wishlist
        Optional<WishlistItem> existingItem = wishlistItemRepository.findByUserIdAndProductId(userId, productId);
        if (existingItem.isPresent()) {
            throw new ResourceAlreadyExists("Product already exists in wishlist");
        }

        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setUser(user);
        wishlistItem.setProduct(product);
        // createdAt is automatically set by @CreationTimestamp

        return wishlistItemRepository.save(wishlistItem);
    }

    public void removeFromWishlist(Long userId, Long productId) {
        WishlistItem wishlistItem = wishlistItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item", "id", productId));

        wishlistItemRepository.delete(wishlistItem);
    }

    public void clearWishlist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        List<WishlistItem> items = wishlistItemRepository.findByUserId(userId);
        wishlistItemRepository.deleteAll(items);
    }

    public boolean isProductInWishlist(Long userId, Long productId) {
        return wishlistItemRepository.existsByUserIdAndProductId(userId, productId);
    }

    public long getWishlistItemCount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return wishlistItemRepository.countByUserId(userId);
    }

    public List<Product> getWishlistProducts(Long userId) {
        List<WishlistItem> wishlistItems = getUserWishlist(userId);
        return wishlistItems.stream()
                .map(WishlistItem::getProduct)
                .toList();
    }

    public void moveToCart(Long userId, Long productId) {
        WishlistItem wishlistItem = wishlistItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item", "id", productId));

        // Here you would typically call CartService to add the product
        // For now, we'll just remove it from wishlist
        wishlistItemRepository.delete(wishlistItem);
    }

    public List<WishlistItem> getWishlistItemsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return wishlistItemRepository.findByProductId(productId);
    }

    public void removeWishlistItemById(Long userId, Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item", "id", wishlistItemId));

        if (!wishlistItem.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Wishlist item", "id", wishlistItemId);
        }

        wishlistItemRepository.delete(wishlistItem);
    }
}
