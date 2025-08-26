package com.project.controller;

import com.project.dto.AddToCartRequest;
import com.project.dto.UpdateCartRequest;
import com.project.entity.CartItem;
import com.project.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart Management", description = "APIs for managing shopping cart")
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get cart items", description = "Retrieve all items in the user's cart")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart", description = "Add a product to the shopping cart")
    public ResponseEntity<CartItem> addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an item in the cart")
    public ResponseEntity<Void> updateCartItem(@Valid @RequestBody UpdateCartRequest request) {
        cartService.updateCartItemQuantity(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove item from cart", description = "Remove an item from the shopping cart")
    public ResponseEntity<Void> removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total")
    @Operation(summary = "Get cart total", description = "Calculate the total cost of items in the cart")
    public ResponseEntity<BigDecimal> getCartTotal(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartTotal(userId));
    }

    @GetMapping("/count")
    @Operation(summary = "Get cart item count", description = "Get the total number of items in the cart")
    public ResponseEntity<Long> getCartItemCount(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartItemCount(userId));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
