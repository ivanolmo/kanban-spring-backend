package com.ivanolmo.kanbantaskmanager.dto.validation;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SubtaskValidator implements ConstraintValidator<ValidSubtask, SubtaskDTO> {
  private static final int MIN_TITLE_LENGTH = 3;
  private static final int MAX_TITLE_LENGTH = 50;

  @Override
  public boolean isValid(SubtaskDTO value, ConstraintValidatorContext context) {
    if (value.getTitle() != null) {
      int titleLength = value.getTitle().length();
      if (titleLength < MIN_TITLE_LENGTH || titleLength > MAX_TITLE_LENGTH) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Subtask title should be between 3 and 50 characters")
            .addPropertyNode("title")
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }
}
