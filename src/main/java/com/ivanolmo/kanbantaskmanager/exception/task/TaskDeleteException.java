package com.ivanolmo.kanbantaskmanager.exception.task;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TaskDeleteException extends BaseException {
  public TaskDeleteException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public TaskDeleteException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
