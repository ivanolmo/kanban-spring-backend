package com.ivanolmo.kanbantaskmanager.exception.board;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BoardDeleteException extends BaseException {
  public BoardDeleteException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public BoardDeleteException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
