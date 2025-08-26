package com.project.service;

import com.project.dto.RegisterRequest;
import com.project.entity.User;
import com.project.enums.UserType;
import com.project.exception.ResourceAlreadyExists;
import com.project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExists("Email already in use!");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUserType(UserType.USER);
        
        // Set default values for required fields
        user.setIsActive(true);
        user.setPhone(""); // Default empty phone number
        
        userRepository.save(user);
    }
}
