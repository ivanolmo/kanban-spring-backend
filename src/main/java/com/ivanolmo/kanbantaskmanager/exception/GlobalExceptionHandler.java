package com.ivanolmo.kanbantaskmanager.exception;

import com.ivanolmo.kanbantaskmanager.exception.board.*;
import com.ivanolmo.kanbantaskmanager.exception.column.*;
import com.ivanolmo.kanbantaskmanager.exception.task.*;
import com.ivanolmo.kanbantaskmanager.exception.user.UserNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BoardNotFoundException.class)
  public ResponseEntity<String> handleBoardNotFoundException(BoardNotFoundException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BoardCreationFailedException.class)
  public ResponseEntity<String> handleBoardCreationFailedException(BoardCreationFailedException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BoardUpdateException.class)
  public ResponseEntity<String> handleBoardUpdateException(BoardUpdateException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BoardAlreadyExistsException.class)
  public ResponseEntity<String> handleBoardAlreadyExistsException(BoardAlreadyExistsException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BoardDeleteException.class)
  public ResponseEntity<String> handleBoardDeleteException(BoardDeleteException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ColumnNotFoundException.class)
  public ResponseEntity<String> handleColumnNotFoundException(ColumnNotFoundException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ColumnCreationFailedException.class)
  public ResponseEntity<String> handleColumnCreationFailedException(ColumnCreationFailedException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ColumnUpdateException.class)
  public ResponseEntity<String> handleColumnUpdateException(ColumnUpdateException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ColumnAlreadyExistsException.class)
  public ResponseEntity<String> handleColumnAlreadyExistsException(ColumnAlreadyExistsException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ColumnDeleteException.class)
  public ResponseEntity<String> handleColumnDeleteException(ColumnDeleteException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TaskCreationException.class)
  public ResponseEntity<String> handleTaskCreationException(TaskCreationException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TaskUpdateException.class)
  public ResponseEntity<String> handleTaskUpdateException(TaskUpdateException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TaskDataAlreadyExistsException.class)
  public ResponseEntity<String> handleTaskDataAlreadyExistsException(TaskDataAlreadyExistsException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(TaskDeleteException.class)
  public ResponseEntity<String> handleTaskDeleteException(TaskDeleteException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
    List<String> errors = e.getBindingResult()
        .getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAllExceptions(Exception e) {
    return new ResponseEntity<>("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
