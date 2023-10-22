package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.config.JwtService;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationResponseDTO;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.AuthenticationException;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public AuthenticationResponseDTO register(AuthenticationRequestDTO request) {
    // if user email is already in use, throw error
    userRepository.findByEmail(request.getEmail().toLowerCase())
        .ifPresent(user -> {
          throw new AuthenticationException("Email is already in use", HttpStatus.BAD_REQUEST);
        });

    User user = User
        .builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();

    userRepository.save(user);

    UserDetails userDetails = new UserDetailsImpl(user);

    return AuthenticationResponseDTO
        .builder()
        .userId(user.getId())
        .email(user.getEmail())
        .accessToken(jwtService.generateToken(userDetails))
        .build();
  }

  public AuthenticationResponseDTO login(AuthenticationRequestDTO request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );
    } catch (BadCredentialsException e) {
      throw new AuthenticationException("Invalid email or password", e, HttpStatus.UNAUTHORIZED);
    }

    User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
        new AuthenticationException("User not found", HttpStatus.NOT_FOUND));

    UserDetails userDetails = new UserDetailsImpl(user);

    return AuthenticationResponseDTO
        .builder()
        .userId(user.getId())
        .email(user.getEmail())
        .accessToken(jwtService.generateToken(userDetails))
        .build();
  }
}
