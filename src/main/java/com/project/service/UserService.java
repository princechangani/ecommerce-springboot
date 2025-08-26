package com.project.service;

import com.project.dto.UpdateUserProfileRequest;
import com.project.dto.UserProfileDto;
import com.project.entity.Address;
import com.project.entity.User;
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

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
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

    public UserProfileDto updateUserProfile(Long userId, UpdateUserProfileRequest request) {
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
        
        return new UserProfileDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getPhone(),
                savedUser.getUserType(),
                savedUser.getIsActive()
        );
    }

    public Address addUserAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        address.setUser(user);
        return addressRepository.save(address);
    }

    public List<Address> getUserAddresses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return user.getAddresses();
    }

    public Address updateUserAddress(Long userId, Long addressId, Address addressDetails) {
        Address address = addressRepository.findById(addressId)
                .filter(addr -> addr.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        
        if (addressDetails.getAddressLine1() != null) {
            address.setAddressLine1(addressDetails.getAddressLine1());
        }
        if (addressDetails.getAddressLine2() != null) {
            address.setAddressLine2(addressDetails.getAddressLine2());
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
        
        return addressRepository.save(address);
    }

    public void deleteUserAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .filter(addr -> addr.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        
        addressRepository.delete(address);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userRepository.deleteById(userId);
    }
}
