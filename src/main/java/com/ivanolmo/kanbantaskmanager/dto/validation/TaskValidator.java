package com.ivanolmo.kanbantaskmanager.dto.validation;

import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskValidator implements ConstraintValidator<ValidTask, TaskDTO> {
  private static final int MIN_LENGTH = 3;
  private static final int MAX_TITLE_LENGTH = 50;
  private static final int MAX_DESCRIPTION_LENGTH = 255;

  @Override
  public boolean isValid(TaskDTO value, ConstraintValidatorContext context) {
    boolean isValid = true;

    if (value.getTitle() == null || value.getTitle().isEmpty()) {
      context.buildConstraintViolationWithTemplate("Task title cannot be blank.")
          .addPropertyNode("title")
          .addConstraintViolation();
      isValid = false;
    } else {
      int titleLength = value.getTitle().length();
      if (titleLength < MIN_LENGTH || titleLength > MAX_TITLE_LENGTH) {
        context.buildConstraintViolationWithTemplate("Task title should be between 3 and 50 characters.")
            .addPropertyNode("title")
            .addConstraintViolation();
        isValid = false;
      }
    }

    if (value.getDescription() == null || value.getDescription().isEmpty()) {
      context.buildConstraintViolationWithTemplate("Task description cannot be blank.")
          .addPropertyNode("description")
          .addConstraintViolation();
      isValid = false;
    } else {
      int descriptionLength = value.getDescription().length();
      if (descriptionLength < MIN_LENGTH || descriptionLength > MAX_DESCRIPTION_LENGTH) {
        context.buildConstraintViolationWithTemplate("Task description should be between 3 and 255 characters.")
            .addPropertyNode("description")
            .addConstraintViolation();
        isValid = false;
      }
    }

    if (!isValid) {
      context.disableDefaultConstraintViolation();
    }

    return isValid;
  }
}
