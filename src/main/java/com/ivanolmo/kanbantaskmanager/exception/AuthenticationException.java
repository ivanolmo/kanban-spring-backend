package com.ivanolmo.kanbantaskmanager.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
  public AuthenticationException(String message, HttpStatus status) {
    super(message, status);
  }

  public AuthenticationException(String message, Throwable cause, HttpStatus status) {
    super(message, cause, status);
  }
}
