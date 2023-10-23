package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationResponseDTO;

public interface AuthenticationService {
  AuthenticationResponseDTO register(AuthenticationRequestDTO request);
  AuthenticationResponseDTO login(AuthenticationRequestDTO request);

}
