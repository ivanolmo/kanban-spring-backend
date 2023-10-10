package com.ivanolmo.kanbantaskmanager.exception.board;

public class BoardUpdateException extends RuntimeException{
  public BoardUpdateException(String message) {
    super(message);
  }

  public BoardUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
