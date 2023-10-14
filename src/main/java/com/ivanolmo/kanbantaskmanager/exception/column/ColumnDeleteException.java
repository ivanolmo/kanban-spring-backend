package com.ivanolmo.kanbantaskmanager.exception.column;

public class ColumnDeleteException extends RuntimeException {
  public ColumnDeleteException(String message) {
    super(message);
  }

  public ColumnDeleteException(String message, Throwable cause) {
    super(message, cause);
  }
}
