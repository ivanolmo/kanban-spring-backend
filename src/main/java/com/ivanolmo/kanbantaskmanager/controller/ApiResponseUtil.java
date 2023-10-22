package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtil {
  public static <T> ResponseEntity<ApiResponse<T>> buildSuccessResponse(T data,
                                                                        String message,
                                                                        HttpStatus status) {
    ApiResponse<T> apiResponse = ApiResponse.<T>builder()
        .data(data)
        .success(true)
        .message(message)
        .status(status)
        .build();
    return new ResponseEntity<>(apiResponse, status);
  }

  public static ResponseEntity<ApiResponse<Object>> buildErrorResponse(ApiError error,
                                                                       String message,
                                                                       HttpStatus status) {
    ApiResponse<Object> apiResponse = ApiResponse.builder()
        .error(error)
        .success(false)
        .message(message)
        .status(status)
        .build();
    return new ResponseEntity<>(apiResponse, status);
  }
}