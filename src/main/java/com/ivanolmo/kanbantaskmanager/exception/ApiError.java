package com.ivanolmo.kanbantaskmanager.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
}
