package com.ivanolmo.kanbantaskmanager.exception.board;

public class BoardCreationFailedException extends RuntimeException {
  public BoardCreationFailedException(String message) {
    super(message);
  }

  public BoardCreationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
