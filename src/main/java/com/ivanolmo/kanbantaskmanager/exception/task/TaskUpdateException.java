package com.ivanolmo.kanbantaskmanager.exception.task;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TaskUpdateException extends BaseException {
  public TaskUpdateException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public TaskUpdateException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
