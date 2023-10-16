package com.ivanolmo.kanbantaskmanager.exception.column;

public class ColumnCreationException extends RuntimeException {
  public ColumnCreationException(String message) {
    super(message);
  }

  public ColumnCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
