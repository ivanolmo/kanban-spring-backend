package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.config.JwtService;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthResponseDTO;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.AuthException;
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
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public AuthResponseDTO register(AuthRequestDTO request) {
    // if user email is already in use, throw error
    userRepository.findByEmail(request.getEmail().toLowerCase())
        .ifPresent(user -> {
          throw new AuthException("Email is already in use", HttpStatus.BAD_REQUEST);
        });

    User user = User
        .builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();

    userRepository.save(user);

    UserDetails userDetails = new UserDetailsImpl(user);

    return AuthResponseDTO
        .builder()
        .userId(user.getId())
        .email(user.getEmail())
        .accessToken(jwtService.generateToken(userDetails))
        .build();
  }

  public AuthResponseDTO login(AuthRequestDTO request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );
    } catch (BadCredentialsException e) {
      throw new AuthException("Invalid email or password", e, HttpStatus.UNAUTHORIZED);
    }

    User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
        new AuthException("User not found", HttpStatus.NOT_FOUND));

    UserDetails userDetails = new UserDetailsImpl(user);

    return AuthResponseDTO
        .builder()
        .userId(user.getId())
        .email(user.getEmail())
        .accessToken(jwtService.generateToken(userDetails))
        .build();
  }
}
