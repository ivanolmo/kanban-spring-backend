package com.ivanolmo.kanbantaskmanager.exception.column;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ColumnAlreadyExistsException extends BaseException {
  public ColumnAlreadyExistsException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public ColumnAlreadyExistsException(String message, Throwable cause) {
    super(message, cause, HttpStatus.CONFLICT);
  }
}
