package com.ivanolmo.kanbantaskmanager.exception;

import org.springframework.http.HttpStatus;

public class EntityOperationException extends BaseException {
  public EntityOperationException(String entity, String operation, HttpStatus status) {
    super(entity + " " + operation + " operation failed", status);
  }

  public EntityOperationException(String entity, String operation, Throwable cause,
                                  HttpStatus status) {
    super(entity + " " + operation + " operation failed", cause, status);
  }

  public EntityOperationException(String customMessage, HttpStatus status) {
    super(customMessage, status);
  }
}
