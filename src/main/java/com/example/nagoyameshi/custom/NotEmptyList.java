package com.example.nagoyameshi.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = NotEmptyListValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyList {
    String message() default "リストは空であってはなりません";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
