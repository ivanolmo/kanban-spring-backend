package com.ivanolmo.kanbantaskmanager.exception.task;

public class TaskCreationException extends RuntimeException {
  public TaskCreationException(String message) {
    super(message);
  }

  public TaskCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
