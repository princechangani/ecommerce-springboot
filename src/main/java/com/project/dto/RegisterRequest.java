package com.project.dto;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.project.validation.PasswordMatches;

@Data
@PasswordMatches
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,100}$",
        message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character"
    )
    private String password;

    @Transient
    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    @Size(max = 20, message = "Phone number must be less than 20 characters")
    @Pattern(regexp = "^[0-9\\-\\+\\(\\)\\s]*$", message = "Please provide a valid phone number")
    private String phone;
}