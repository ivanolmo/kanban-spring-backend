package com.ivanolmo.kanbantaskmanager.exception.column;

public class ColumnCreationFailedException extends RuntimeException {
  public ColumnCreationFailedException(String message) {
    super(message);
  }

  public ColumnCreationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
