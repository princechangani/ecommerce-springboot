package com.project.controller;

import com.project.dto.UpdateUserProfileRequest;
import com.project.dto.UserProfileDto;
import com.project.entity.User;
import com.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> updateCurrentUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(userService.updateUserProfile(user.getId(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @GetMapping("/admin/all")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        
        List<UserProfileDto> profiles = users.stream()
                .map(user -> userService.getUserProfile(user.getId()))
                .toList();
        
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/admin/{id}/status")
    @Operation(summary = "Update user status", description = "Update the active status of a user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestParam boolean isActive) {
        // This functionality needs to be added to UserService
        // For now, we'll use a simple approach
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(isActive);
        userService.saveUser(user);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/count")
    @Operation(summary = "Get user count", description = "Get the count of active users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getActiveUserCount() {
        // This functionality needs to be added to UserService
        // For now, we'll count active users from the list
        long activeCount = userService.getAllUsers().stream()
                .filter(User::getIsActive)
                .count();
        return ResponseEntity.ok(activeCount);
    }

    @GetMapping("/{userId}/addresses")
    @Operation(summary = "Get user addresses", description = "Get all addresses for a specific user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<com.project.entity.Address>> getUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserAddresses(userId));
    }

    @PostMapping("/{userId}/addresses")
    @Operation(summary = "Add user address", description = "Add a new address for a user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<com.project.entity.Address> addUserAddress(
            @PathVariable Long userId,
            @Valid @RequestBody com.project.entity.Address address) {
        return ResponseEntity.ok(userService.addUserAddress(userId, address));
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    @Operation(summary = "Update user address", description = "Update an existing address for a user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<com.project.entity.Address> updateUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody com.project.entity.Address addressDetails) {
        return ResponseEntity.ok(userService.updateUserAddress(userId, addressId, addressDetails));
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @Operation(summary = "Delete user address", description = "Delete an address for a user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        userService.deleteUserAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}
