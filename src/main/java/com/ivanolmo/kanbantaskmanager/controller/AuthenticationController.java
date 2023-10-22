package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationResponseDTO;
import com.ivanolmo.kanbantaskmanager.service.AuthenticationService;
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
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthenticationResponseDTO>> register(
      @RequestBody AuthenticationRequestDTO request
  ) {
    AuthenticationResponseDTO response = authenticationService.register(request);

    return ApiResponseUtil.buildSuccessResponse(
        response, "Successful user creation", HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthenticationResponseDTO>> login(
      @RequestBody AuthenticationRequestDTO request
  ) {
    AuthenticationResponseDTO response = authenticationService.login(request);
    return ApiResponseUtil.buildSuccessResponse(
        response, "Successful user login", HttpStatus.OK);
  }
}
