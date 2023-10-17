package com.ivanolmo.kanbantaskmanager.exception;

import com.ivanolmo.kanbantaskmanager.exception.board.*;
import com.ivanolmo.kanbantaskmanager.exception.column.*;
import com.ivanolmo.kanbantaskmanager.exception.subtask.*;
import com.ivanolmo.kanbantaskmanager.exception.task.*;
import com.ivanolmo.kanbantaskmanager.exception.user.UserNotFoundException;
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
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e,
                                                            HttpServletRequest request) {
    log.error("UserNotFoundException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(BoardNotFoundException.class)
  public ResponseEntity<Object> handleBoardNotFoundException(BoardNotFoundException e,
                                                             HttpServletRequest request) {
    log.error("BoardNotFoundException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(BoardCreationException.class)
  public ResponseEntity<Object> handleBoardCreationException(BoardCreationException e,
                                                             HttpServletRequest request) {
    log.error("BoardCreationException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(BoardUpdateException.class)
  public ResponseEntity<Object> handleBoardUpdateException(BoardUpdateException e,
                                                           HttpServletRequest request) {
    log.error("BoardUpdateException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(BoardAlreadyExistsException.class)
  public ResponseEntity<Object> handleBoardAlreadyExistsException(BoardAlreadyExistsException e,
                                                                  HttpServletRequest request) {
    log.error("BoardAlreadyExistsException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(BoardDeleteException.class)
  public ResponseEntity<Object> handleBoardDeleteException(BoardDeleteException e,
                                                           HttpServletRequest request) {
    log.error("BoardDeleteException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ColumnNotFoundException.class)
  public ResponseEntity<Object> handleColumnNotFoundException(ColumnNotFoundException e,
                                                              HttpServletRequest request) {
    log.error("ColumnNotFoundException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ColumnCreationException.class)
  public ResponseEntity<Object> handleColumnCreationException(ColumnCreationException e,
                                                              HttpServletRequest request) {
    log.error("ColumnCreationException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ColumnUpdateException.class)
  public ResponseEntity<Object> handleColumnUpdateException(ColumnUpdateException e,
                                                            HttpServletRequest request) {
    log.error("ColumnUpdateException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(ColumnAlreadyExistsException.class)
  public ResponseEntity<Object> handleColumnAlreadyExistsException(ColumnAlreadyExistsException e
      ,
                                                                   HttpServletRequest request) {
    log.error("ColumnAlreadyExistsException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(ColumnDeleteException.class)
  public ResponseEntity<Object> handleColumnDeleteException(ColumnDeleteException e,
                                                            HttpServletRequest request) {
    log.error("ColumnDeleteException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<Object> handleTaskNotFoundException(TaskNotFoundException e,
                                                            HttpServletRequest request) {
    log.error("TaskNotFoundException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(TaskCreationException.class)
  public ResponseEntity<Object> handleTaskCreationException(TaskCreationException e,
                                                            HttpServletRequest request) {
    log.error("TaskCreationException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(TaskUpdateException.class)
  public ResponseEntity<Object> handleTaskUpdateException(TaskUpdateException e,
                                                          HttpServletRequest request) {
    log.error("TaskUpdateException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(TaskDataAlreadyExistsException.class)
  public ResponseEntity<Object> handleTaskDataAlreadyExistsException(TaskDataAlreadyExistsException e,
                                                                     HttpServletRequest request) {
    log.error("TaskDataAlreadyExistsException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(TaskDeleteException.class)
  public ResponseEntity<Object> handleTaskDeleteException(TaskDeleteException e,
                                                          HttpServletRequest request) {
    log.error("TaskDeleteException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(SubtaskNotFoundException.class)
  public ResponseEntity<Object> handleSubtaskNotFoundException(SubtaskNotFoundException e,
                                                               HttpServletRequest request) {
    log.error("SubtaskNotFoundException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(SubtaskCreationException.class)
  public ResponseEntity<Object> handleSubtaskCreationException(SubtaskCreationException e,
                                                               HttpServletRequest request) {
    log.error("SubtaskCreationException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(SubtaskUpdateException.class)
  public ResponseEntity<Object> handleSubtaskUpdateException(SubtaskUpdateException e,
                                                             HttpServletRequest request) {
    log.error("SubtaskUpdateException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(SubtaskDataAlreadyExistsException.class)
  public ResponseEntity<Object> handleSubtaskDataAlreadyExistsException(SubtaskDataAlreadyExistsException e,
                                                                        HttpServletRequest request) {
    log.error("SubtaskDataAlreadyExistsException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(SubtaskDeleteException.class)
  public ResponseEntity<Object> handleSubtaskDeleteException(SubtaskDeleteException e,
                                                             HttpServletRequest request) {
    log.error("SubtaskDeleteException occurred: {}", e.getMessage());
    return createResponseEntity(e, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e,
                                                           HttpServletRequest request) {
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
    log.error("An error occurred: {}", e.getMessage(), e);
    return createResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  private ResponseEntity<Object> createResponseEntity(Exception e,
                                                      HttpStatus status,
                                                      HttpServletRequest request) {
    ApiError apiError = new ApiError();
    apiError.setTimestamp(LocalDateTime.now());
    apiError.setStatus(status.value());
    apiError.setError(status.getReasonPhrase());
    apiError.setMessage(e.getMessage());
    apiError.setPath(request.getRequestURI());

    return new ResponseEntity<>(apiError, status);
  }
}
