package com.project.controller;

import com.project.dto.RegisterRequest;
import com.project.service.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRegistrationService userRegistrationService;

    @InjectMocks
    private WebController webController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    void showHomePage_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void showLoginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void showLoginPage_WithError_ShouldAddErrorToModel() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void showLoginPage_WithLogout_ShouldAddMessageToModel() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void showRegistrationForm_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void registerUser_WithValidData_ShouldRedirectToLogin() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Test@123");
        registerRequest.setConfirmPassword("Test@123");

        // No need to mock the return value since registerUser is void
        doNothing().when(userRegistrationService).registerUser(any(RegisterRequest.class));

        mockMvc.perform(post("/register")
                .param("firstName", registerRequest.getFirstName())
                .param("lastName", registerRequest.getLastName())
                .param("email", registerRequest.getEmail())
                .param("password", registerRequest.getPassword())
                .param("confirmPassword", registerRequest.getConfirmPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));
    }

    @Test
    void registerUser_WithInvalidData_ShouldReturnToRegisterView() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", "")
                .param("lastName", "")
                .param("email", "invalid-email")
                .param("password", "short")
                .param("confirmPassword", "mismatch"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }
}

