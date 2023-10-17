package com.ivanolmo.kanbantaskmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public abstract class BaseException extends RuntimeException {
  private final HttpStatus status;

  public BaseException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public BaseException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }
}
