package com.project.validation;

import com.project.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator 
  implements ConstraintValidator<PasswordMatches, Object> { 
    
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {       
    }
    
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){   
        RegisterRequest user = (RegisterRequest) obj;
        return user.getPassword().equals(user.getConfirmPassword());    
    }     
}
