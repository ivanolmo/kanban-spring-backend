package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.auth.AuthRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthResponseDTO;

public interface AuthService {
  AuthResponseDTO register(AuthRequestDTO request);
  AuthResponseDTO login(AuthRequestDTO request);

}
