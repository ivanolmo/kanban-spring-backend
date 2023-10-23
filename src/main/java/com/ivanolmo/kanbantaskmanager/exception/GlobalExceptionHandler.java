package com.ivanolmo.kanbantaskmanager.exception;

import com.ivanolmo.kanbantaskmanager.controller.ApiResponse;
import com.ivanolmo.kanbantaskmanager.controller.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(EntityOperationException.class)
  public ResponseEntity<ApiResponse<Object>> handleEntityOperationException(EntityOperationException e,
                                                                            HttpServletRequest request) {
    return createResponseEntity(e, e.getHttpStatus(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException e,
                                                                        HttpServletRequest request) {
    log.error("Validation errors occurred: {}", e.getMessage(), e);

    List<String> errors = e.getBindingResult()
        .getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList();

    String errorMessage = "Validation failed";

    return createResponseEntity(e, errorMessage, HttpStatus.BAD_REQUEST, request, errors);
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthException e,
                                                                           HttpServletRequest request) {
    log.error("Authentication errors occurred: {}", e.getMessage(), e);

    return createResponseEntity(e, e.getHttpStatus(), request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception e,
                                                                 HttpServletRequest request) {
    return createResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  private ResponseEntity<ApiResponse<Object>> createResponseEntity(Exception e,
                                                                   String errorMessage,
                                                                   HttpStatus status,
                                                                   HttpServletRequest request,
                                                                   List<String> validationErrors) {
    log.error("{} occurred: {}", e.getClass().getSimpleName(), e.getMessage(), e);

    ApiError apiError = ApiError
        .builder()
        .timestamp(LocalDateTime.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(errorMessage)
        .path(request.getRequestURI())
        .validationErrors(validationErrors)
        .build();

    return ApiResponseUtil.buildErrorResponse(apiError, errorMessage, status);
  }

  private ResponseEntity<ApiResponse<Object>> createResponseEntity(Exception e,
                                                                   HttpStatus status,
                                                                   HttpServletRequest request) {
    log.error("{} occurred: {}", e.getClass().getSimpleName(), e.getMessage(), e);

    return createResponseEntity(e, e.getMessage(), status, request, null);
  }
}
