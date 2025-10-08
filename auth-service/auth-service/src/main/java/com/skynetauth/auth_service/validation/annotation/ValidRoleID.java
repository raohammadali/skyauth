package com.skynetauth.auth_service.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.skynetauth.auth_service.validation.validator.ValidRoleIDValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRoleIDValidator.class)
public @interface ValidRoleID {
    String message() default "Invalid Role IDs";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
