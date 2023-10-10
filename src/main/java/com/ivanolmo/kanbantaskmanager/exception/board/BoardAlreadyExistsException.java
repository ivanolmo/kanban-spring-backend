package com.ivanolmo.kanbantaskmanager.exception.board;

public class BoardAlreadyExistsException extends RuntimeException{
  public BoardAlreadyExistsException(String message) {
    super(message);
  }

  public BoardAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
