package com.ivanolmo.kanbantaskmanager.exception.subtask;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SubtaskUpdateException extends BaseException {
  public SubtaskUpdateException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public SubtaskUpdateException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
