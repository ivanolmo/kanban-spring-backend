package com.ivanolmo.kanbantaskmanager.exception.board;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BoardUpdateException extends BaseException {
  public BoardUpdateException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public BoardUpdateException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
