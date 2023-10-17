package com.ivanolmo.kanbantaskmanager.exception.column;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ColumnUpdateException extends BaseException {
  public ColumnUpdateException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public ColumnUpdateException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
