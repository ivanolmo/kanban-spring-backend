package com.ivanolmo.kanbantaskmanager.exception.column;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ColumnCreationException extends BaseException {
  public ColumnCreationException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public ColumnCreationException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
