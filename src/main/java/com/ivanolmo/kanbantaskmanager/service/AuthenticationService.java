package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.config.JwtService;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationResponseDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.RegisterRequestDTO;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public AuthenticationResponseDTO register(RegisterRequestDTO request) {
    User user = User
        .builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
    userRepository.save(user);

    return AuthenticationResponseDTO
        .builder()
        .accessToken(jwtService.generateToken(user))
        .build();
  }

  public AuthenticationResponseDTO login(AuthenticationRequestDTO request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

    return AuthenticationResponseDTO
        .builder()
        .userId(user.getId())
        .email(user.getEmail())
        .accessToken(jwtService.generateToken(user))
        .build();
  }
}
