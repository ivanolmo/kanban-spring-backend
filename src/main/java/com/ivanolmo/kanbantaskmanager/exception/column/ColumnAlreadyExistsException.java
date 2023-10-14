package com.ivanolmo.kanbantaskmanager.exception.column;

public class ColumnAlreadyExistsException extends RuntimeException {
  public ColumnAlreadyExistsException(String message) {
    super(message);
  }

  public ColumnAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
