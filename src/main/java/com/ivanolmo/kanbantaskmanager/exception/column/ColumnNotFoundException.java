package com.ivanolmo.kanbantaskmanager.exception.column;

public class ColumnNotFoundException extends RuntimeException {
  public ColumnNotFoundException(String message) {
    super(message);
  }

  public ColumnNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
