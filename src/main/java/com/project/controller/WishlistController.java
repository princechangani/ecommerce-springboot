package com.project.controller;

import com.project.entity.Product;
import com.project.entity.WishlistItem;
import com.project.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wishlist Management", description = "APIs for managing user wishlists")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get user wishlist", description = "Retrieve all items in the user's wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<WishlistItem>> getUserWishlist(@RequestParam Long userId) {
        List<WishlistItem> wishlist = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    @GetMapping("/products")
    @Operation(summary = "Get wishlist products", description = "Retrieve all products in the user's wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> getWishlistProducts(@RequestParam Long userId) {
        List<Product> products = wishlistService.getWishlistProducts(userId);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/add")
    @Operation(summary = "Add product to wishlist", description = "Add a product to the user's wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WishlistItem> addToWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        WishlistItem wishlistItem = wishlistService.addToWishlist(userId, productId);
        return ResponseEntity.ok(wishlistItem);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove product from wishlist", description = "Remove a product from the user's wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeFromWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{wishlistItemId}")
    @Operation(summary = "Remove wishlist item by ID", description = "Remove a specific wishlist item by its ID")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeWishlistItem(
            @RequestParam Long userId,
            @PathVariable Long wishlistItemId) {
        wishlistService.removeWishlistItemById(userId, wishlistItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear wishlist", description = "Remove all items from the user's wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> clearWishlist(@RequestParam Long userId) {
        wishlistService.clearWishlist(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    @Operation(summary = "Check if product in wishlist", description = "Check if a product is in the user's wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> isProductInWishlist(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        boolean isInWishlist = wishlistService.isProductInWishlist(userId, productId);
        return ResponseEntity.ok(isInWishlist);
    }

    @GetMapping("/count")
    @Operation(summary = "Get wishlist item count", description = "Get the total number of items in the user's wishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> getWishlistItemCount(@RequestParam Long userId) {
        long count = wishlistService.getWishlistItemCount(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/move-to-cart")
    @Operation(summary = "Move item to cart", description = "Move a wishlist item to the shopping cart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> moveToCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        wishlistService.moveToCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-product/{productId}")
    @Operation(summary = "Get wishlist items by product", description = "Get all wishlist items for a specific product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WishlistItem>> getWishlistItemsByProduct(@PathVariable Long productId) {
        List<WishlistItem> items = wishlistService.getWishlistItemsByProduct(productId);
        return ResponseEntity.ok(items);
    }
}
