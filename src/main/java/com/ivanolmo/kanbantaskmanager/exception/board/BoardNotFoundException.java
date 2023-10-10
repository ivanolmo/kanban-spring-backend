package com.ivanolmo.kanbantaskmanager.exception.board;

public class BoardNotFoundException extends RuntimeException {
  public BoardNotFoundException(String message) {
    super(message);
  }

  public BoardNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
