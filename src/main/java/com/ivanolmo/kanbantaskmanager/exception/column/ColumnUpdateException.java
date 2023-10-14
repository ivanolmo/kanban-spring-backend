package com.ivanolmo.kanbantaskmanager.exception.column;

public class ColumnUpdateException extends RuntimeException {
  public ColumnUpdateException(String message) {
    super(message);
  }

  public ColumnUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
