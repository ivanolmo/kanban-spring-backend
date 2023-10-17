package com.ivanolmo.kanbantaskmanager.exception.board;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BoardCreationException extends BaseException {
  public BoardCreationException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public BoardCreationException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
