package com.ivanolmo.kanbantaskmanager.exception.board;

public class BoardDeleteException extends RuntimeException {
  public BoardDeleteException(String message) {
    super(message);
  }

  public BoardDeleteException(String message, Throwable cause) {
    super(message, cause);
  }
}
