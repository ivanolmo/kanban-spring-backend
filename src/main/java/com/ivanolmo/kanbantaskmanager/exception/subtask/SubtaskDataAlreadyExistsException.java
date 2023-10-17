package com.ivanolmo.kanbantaskmanager.exception.subtask;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SubtaskDataAlreadyExistsException extends BaseException {
  public SubtaskDataAlreadyExistsException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public SubtaskDataAlreadyExistsException(String message, Throwable cause) {
    super(message, cause, HttpStatus.CONFLICT);
  }
}
