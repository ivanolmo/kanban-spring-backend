package com.ivanolmo.kanbantaskmanager.exception.task;

public class TaskDataAlreadyExistsException extends RuntimeException {
  public TaskDataAlreadyExistsException(String message) {
    super(message);
  }

  public TaskDataAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
