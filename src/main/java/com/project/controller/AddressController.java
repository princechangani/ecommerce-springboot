package com.project.controller;

import com.project.entity.Address;
import com.project.service.AddressService;
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
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Address Management", description = "APIs for managing user addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user addresses", description = "Retrieve all addresses for a specific user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Address>> getUserAddresses(@PathVariable Long userId) {
        List<Address> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address by ID", description = "Retrieve a specific address by its ID")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Address> getAddressById(@PathVariable Long addressId) {
        Address address = addressService.getAddressById(addressId);
        return ResponseEntity.ok(address);
    }

    @PostMapping("/user/{userId}")
    @Operation(summary = "Create new address", description = "Create a new address for a user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Address> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody Address address) {
        Address createdAddress = addressService.createAddress(userId, address);
        return ResponseEntity.ok(createdAddress);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update address", description = "Update an existing address")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Address> updateAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody Address addressDetails) {
        Address updatedAddress = addressService.updateAddress(userId, addressId, addressDetails);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete address", description = "Delete an address")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/set-default")
    @Operation(summary = "Set default address", description = "Set an address as the default address")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Address> setDefaultAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId) {
        Address defaultAddress = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(defaultAddress);
    }

    @GetMapping("/user/{userId}/default")
    @Operation(summary = "Get default address", description = "Get the default address for a user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Address> getDefaultAddress(@PathVariable Long userId) {
        Address defaultAddress = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(defaultAddress);
    }

    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "Get addresses by type", description = "Get addresses for a user by type (shipping/billing)")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Address>> getAddressesByType(
            @PathVariable Long userId,
            @PathVariable String type) {
        List<Address> addresses = addressService.getAddressesByType(userId, type);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/user/{userId}/type/{type}/default")
    @Operation(summary = "Get default address by type", description = "Get the default address for a user by type")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Address> getDefaultAddressByType(
            @PathVariable Long userId,
            @PathVariable String type) {
        Address defaultAddress = addressService.getDefaultAddressByType(userId, type);
        return ResponseEntity.ok(defaultAddress);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate address", description = "Validate an address")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> validateAddress(@Valid @RequestBody Address address) {
        boolean isValid = addressService.validateAddress(address);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Get address count", description = "Get the total number of addresses for a user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> getAddressCount(@PathVariable Long userId) {
        long count = addressService.getAddressCount(userId);
        return ResponseEntity.ok(count);
    }
}
