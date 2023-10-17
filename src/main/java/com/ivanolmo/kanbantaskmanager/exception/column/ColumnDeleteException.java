package com.ivanolmo.kanbantaskmanager.exception.column;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ColumnDeleteException extends BaseException {
  public ColumnDeleteException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public ColumnDeleteException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
