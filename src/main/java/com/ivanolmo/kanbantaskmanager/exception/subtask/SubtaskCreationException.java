package com.ivanolmo.kanbantaskmanager.exception.subtask;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SubtaskCreationException extends BaseException {
  public SubtaskCreationException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public SubtaskCreationException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
