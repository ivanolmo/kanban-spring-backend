package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.config.JwtService;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationResponseDTO;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class AuthenticationServiceTest {
  @MockBean
  private AuthenticationManager authenticationManager;
  @MockBean
  private BCryptPasswordEncoder passwordEncoder;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;
  @Autowired
  private AuthenticationService authenticationService;

  @Test
  public void testRegister() {
    // given
    AuthenticationRequestDTO request = new AuthenticationRequestDTO();
    request.setEmail("test@example.com");
    request.setPassword("password");

    User user = User.builder().email(request.getEmail()).password(request.getPassword()).build();

    // when
    when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
    when(jwtService.generateToken(any(User.class))).thenReturn("token");

    // then
    AuthenticationResponseDTO response = authenticationService.register(request);
    assertEquals(user.getId(), response.getUserId(), "User ID should match");
    assertEquals(user.getEmail(), response.getEmail(), "User email should match");
    assertEquals("token", response.getAccessToken(), "User token should match");

    // verify interactions
    verify(passwordEncoder).encode(request.getPassword());
    verify(jwtService).generateToken(any(User.class));
  }

  @Test
  public void testLogin() {
    // given
    AuthenticationRequestDTO request = new AuthenticationRequestDTO();
    request.setEmail("test@example.com");
    request.setPassword("password");

    User user = User.builder().email(request.getEmail()).password(request.getPassword()).build();

    Authentication mockAuthentication = Mockito.mock(Authentication.class);
    UsernamePasswordAuthenticationToken credentials =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    // when
    when(authenticationManager.authenticate(credentials)).thenReturn(mockAuthentication);
    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
    when(jwtService.generateToken(any(User.class))).thenReturn("token");

    // then
    AuthenticationResponseDTO response = authenticationService.login(request);
    assertEquals(user.getId(), response.getUserId(), "User ID should match");
    assertEquals(user.getEmail(), response.getEmail(), "Email should match");
    assertEquals("token", response.getAccessToken(), "Access token should match");

    // verify interactions
    verify(authenticationManager).authenticate(credentials);
    verify(userRepository).findByEmail(request.getEmail());
    verify(jwtService).generateToken(any(User.class));
  }
}
