package com.ivanolmo.kanbantaskmanager.exception.column;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ColumnNotFoundException extends BaseException {
  public ColumnNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public ColumnNotFoundException(String message, Throwable cause) {
    super(message, cause, HttpStatus.NOT_FOUND);
  }
}
