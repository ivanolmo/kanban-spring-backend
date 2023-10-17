package com.ivanolmo.kanbantaskmanager.exception.task;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TaskCreationException extends BaseException {
  public TaskCreationException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public TaskCreationException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
