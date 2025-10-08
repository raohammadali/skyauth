package com.skynetauth.auth_service.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.skynetauth.auth_service.validation.validator.ValidDistributionIDValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDistributionIDValidator.class)
public @interface ValidDistributionID {
    String message() default "Invalid Distribution IDs";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
