package com.project.service;

import com.project.dto.RegisterRequest;
import com.project.dto.UpdateUserProfileRequest;
import com.project.dto.UserProfileDto;
import com.project.entity.Address;
import com.project.entity.User;
import com.project.enums.UserType;
import com.project.exception.ResourceAlreadyExists;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.AddressRepository;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserProfileDto getUserProfile(Long userId) {
        log.debug("Fetching user profile for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return mapToUserProfileDto(user);
    }

    public UserProfileDto updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        log.debug("Updating profile for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepository.save(user);
        log.info("Updated profile for user: {}", savedUser.getEmail());
        
        return mapToUserProfileDto(savedUser);
    }

    public List<Address> getUserAddresses(Long userId) {
        log.debug("Fetching addresses for user ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return addressRepository.findByUserId(userId);
    }

    public Address addUserAddress(Long userId, Address address) {
        log.debug("Adding address for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        log.info("Added new address ID: {} for user ID: {}", savedAddress.getId(), userId);
        
        return savedAddress;
    }
    
    public void deleteUserAddress(Long userId, Long addressId) {
        log.debug("Deleting address ID: {} for user ID: {}", addressId, userId);
        
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        // Verify address exists and belongs to user
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
                
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address", "id", addressId);
        }
        
        addressRepository.delete(address);
        log.info("Deleted address ID: {} for user ID: {}", addressId, userId);
    }
    
    public Address updateUserAddress(Long userId, Long addressId, Address addressDetails) {
        log.debug("Updating address ID: {} for user ID: {}", addressId, userId);
        
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
                
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address", "id", addressId);
        }
        

        if (addressDetails.getCity() != null) {
            address.setCity(addressDetails.getCity());
        }
        if (addressDetails.getState() != null) {
            address.setState(addressDetails.getState());
        }
        if (addressDetails.getPostalCode() != null) {
            address.setPostalCode(addressDetails.getPostalCode());
        }
        if (addressDetails.getCountry() != null) {
            address.setCountry(addressDetails.getCountry());
        }
        if (addressDetails.getIsDefault() != null) {
            address.setIsDefault(addressDetails.getIsDefault());
        }
        
        Address updatedAddress = addressRepository.save(address);
        log.info("Updated address ID: {} for user ID: {}", addressId, userId);
        
        return updatedAddress;
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        log.debug("Registering new user with email: {}", registerRequest.getEmail());
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExists("Email already in use!");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUserType(UserType.USER);
        user.setIsActive(true);
        user.setPhone("");

        userRepository.save(user);
        log.info("Successfully registered new user: {}", user.getEmail());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    private UserProfileDto mapToUserProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getUserType(),
                user.getIsActive()
        );
    }
}
