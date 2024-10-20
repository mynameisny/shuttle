package me.ningyu.app.easymonger.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = GenderValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender
{
    String message() default "Gender must be MALE or FEMALE or OTHER";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
