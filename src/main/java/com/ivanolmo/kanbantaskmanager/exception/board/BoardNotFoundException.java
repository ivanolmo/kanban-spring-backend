package com.ivanolmo.kanbantaskmanager.exception.board;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BoardNotFoundException extends BaseException {
  public BoardNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public BoardNotFoundException(String message, Throwable cause) {
    super(message, cause, HttpStatus.NOT_FOUND);
  }
}
