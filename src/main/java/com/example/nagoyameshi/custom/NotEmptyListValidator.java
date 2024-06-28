package com.example.nagoyameshi.custom;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, List<?>> {

    @Override
    public void initialize(NotEmptyList constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<?> list, ConstraintValidatorContext context) {
        return list != null && !list.isEmpty();
    }
}
