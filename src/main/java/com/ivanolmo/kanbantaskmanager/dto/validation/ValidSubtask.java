package com.ivanolmo.kanbantaskmanager.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SubtaskValidator.class)
public @interface ValidSubtask {
  String message() default "Title and/or completed must be present";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
