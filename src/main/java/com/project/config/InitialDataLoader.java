package com.project.config;

import com.project.entity.User;
import com.project.enums.UserType;
import com.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InitialDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void loadInitialData() {
        // Only create initial users if no users exist
        if (userRepository.count() == 0) {
            createInitialUsers();
        }
    }

    private void createInitialUsers() {
        // Create admin user
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setUserType(UserType.ADMIN);
        admin.setIsActive(true);

        // Create regular user
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash(passwordEncoder.encode("user123"));
        user.setFirstName("Regular");
        user.setLastName("User");
        user.setUserType(UserType.USER);
        user.setIsActive(true);

        userRepository.saveAll(Arrays.asList(admin, user));
    }
}
