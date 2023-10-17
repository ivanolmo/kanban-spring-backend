package com.ivanolmo.kanbantaskmanager.exception.subtask;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SubtaskDeleteException extends BaseException {
  public SubtaskDeleteException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public SubtaskDeleteException(String message, Throwable cause) {
    super(message, cause, HttpStatus.BAD_REQUEST);
  }
}
