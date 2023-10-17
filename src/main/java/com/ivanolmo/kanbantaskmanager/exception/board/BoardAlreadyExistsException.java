package com.ivanolmo.kanbantaskmanager.exception.board;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BoardAlreadyExistsException extends BaseException {
  public BoardAlreadyExistsException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public BoardAlreadyExistsException(String message, Throwable cause) {
    super(message, cause, HttpStatus.CONFLICT);
  }
}
