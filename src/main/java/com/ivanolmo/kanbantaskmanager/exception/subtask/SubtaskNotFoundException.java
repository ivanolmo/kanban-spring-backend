package com.ivanolmo.kanbantaskmanager.exception.subtask;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SubtaskNotFoundException extends BaseException {
  public SubtaskNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public SubtaskNotFoundException(String message, Throwable cause) {
    super(message, cause, HttpStatus.NOT_FOUND);
  }
}
