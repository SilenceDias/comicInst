package com.example.marvelinst.validations;

import com.example.marvelinst.Annotations.PasswordMatch;
import com.example.marvelinst.payload.request.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class  PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        RegisterRequest registerRequest = (RegisterRequest) obj;
        return registerRequest.getPassword().equals(registerRequest.getConfirmPassword());
    }
}
