package com.ivanolmo.kanbantaskmanager.exception.user;

import com.ivanolmo.kanbantaskmanager.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
  public UserNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause, HttpStatus.NOT_FOUND);
  }
}
