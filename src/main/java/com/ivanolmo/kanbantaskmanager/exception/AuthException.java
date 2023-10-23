package com.ivanolmo.kanbantaskmanager.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {
  public AuthException(String message, HttpStatus status) {
    super(message, status);
  }

  public AuthException(String message, Throwable cause, HttpStatus status) {
    super(message, cause, status);
  }
}
