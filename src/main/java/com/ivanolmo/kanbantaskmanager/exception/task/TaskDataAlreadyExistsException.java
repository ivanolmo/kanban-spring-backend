package com.ivanolmo.kanbantaskmanager.exception.task;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TaskDataAlreadyExistsException extends BaseException {
  public TaskDataAlreadyExistsException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public TaskDataAlreadyExistsException(String message, Throwable cause) {
    super(message, cause, HttpStatus.CONFLICT);
  }
}
