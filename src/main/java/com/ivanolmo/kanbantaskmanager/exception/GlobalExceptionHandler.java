package com.ivanolmo.kanbantaskmanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(EntityOperationException.class)
  public ResponseEntity<Object> handleEntityOperationException(EntityOperationException e,
                                                               HttpServletRequest request) {
    return createResponseEntity(e, e.getHttpStatus(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e,
                                                           HttpServletRequest request) {
    log.error("Validation errors occurred: {}", e.getMessage(), e);

    List<String> errors = e.getBindingResult()
        .getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList();

    ApiError apiError = new ApiError();
    apiError.setTimestamp(LocalDateTime.now());
    apiError.setStatus(HttpStatus.BAD_REQUEST.value());
    apiError.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    apiError.setMessage("Validation errors");
    apiError.setPath(request.getRequestURI());

    Map<String, Object> body = new HashMap<>();
    body.put("apiError", apiError);
    body.put("validationErrors", errors);

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllExceptions(Exception e, HttpServletRequest request) {
    return createResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  private ResponseEntity<Object> createResponseEntity(Exception e,
                                                      HttpStatus status,
                                                      HttpServletRequest request) {
    log.error("{} occurred: {}", e.getClass().getSimpleName(), e.getMessage(), e);

    ApiError apiError = new ApiError();
    apiError.setTimestamp(LocalDateTime.now());
    apiError.setStatus(status.value());
    apiError.setError(status.getReasonPhrase());
    apiError.setMessage(e.getMessage());
    apiError.setPath(request.getRequestURI());

    return new ResponseEntity<>(apiError, status);
  }
}
