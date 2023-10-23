package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.auth.AuthRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthResponseDTO;
import com.ivanolmo.kanbantaskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> register(
      @RequestBody AuthRequestDTO request
  ) {
    AuthResponseDTO response = authService.register(request);

    return ApiResponseUtil.buildSuccessResponse(
        response, "Successful user creation", HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
      @RequestBody AuthRequestDTO request
  ) {
    AuthResponseDTO response = authService.login(request);
    return ApiResponseUtil.buildSuccessResponse(
        response, "Successful user login", HttpStatus.OK);
  }
}
