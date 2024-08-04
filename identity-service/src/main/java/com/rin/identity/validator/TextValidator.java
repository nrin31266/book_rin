package com.rin.identity.validator;

import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TextValidator implements ConstraintValidator<TextConstraint, String> {

    private String value;

    @Override
    public void initialize(TextConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value)) return false;

        return !value.isEmpty();
    }
}
