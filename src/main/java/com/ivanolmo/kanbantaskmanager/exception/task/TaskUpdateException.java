package com.ivanolmo.kanbantaskmanager.exception.task;

public class TaskUpdateException extends RuntimeException {
  public TaskUpdateException(String message) {
    super(message);
  }

  public TaskUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
