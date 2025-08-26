package com.project.controller;

import com.project.dto.RegisterRequest;
import com.project.service.UserRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    private final UserRegistrationService userRegistrationService;

    public TestController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @GetMapping("/create-test-user")
    public String createTestUser() {
        try {
            RegisterRequest testUser = new RegisterRequest();
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setEmail("test@example.com");
            testUser.setPassword("Test@123");
            testUser.setConfirmPassword("Test@123");
            
            userRegistrationService.registerUser(testUser);
            return "redirect:/login?testuser=success";
        } catch (Exception e) {
            return "redirect:/register?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/css-test")
    public String cssTest() {
        return "test";
    }
}
