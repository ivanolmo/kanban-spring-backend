package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.config.JwtService;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.auth.AuthenticationResponseDTO;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.AuthenticationException;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
  @Captor
  private ArgumentCaptor<UserDetailsImpl> userDetailsCaptor;
  AuthenticationRequestDTO request;
  User user;
  String email;
  String password;

  @BeforeEach
  public void setUp() {
    email = "test@example.com";
    password = "password";

    request = new AuthenticationRequestDTO();
    request.setEmail(email);
    request.setPassword(password);

    user = User.builder().email(email).password(password).build();
  }

  @Test
  public void testRegister() {
    // given
    // N/A for this test

    // when
    when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
    when(jwtService.generateToken(userDetailsCaptor.capture())).thenReturn("token");

    // then
    AuthenticationResponseDTO response = authenticationService.register(request);
    UserDetailsImpl capturedUserDetails = userDetailsCaptor.getValue();

    assertEquals(user.getId(), response.getUserId(), "User ID should match");
    assertEquals(user.getEmail(), response.getEmail(), "User email should match");
    assertEquals("token", response.getAccessToken(), "User token should match");
    assertEquals(user.getEmail(), capturedUserDetails.getUsername(), "Usernames should match");

    // verify interactions
    verify(passwordEncoder).encode(request.getPassword());
    verify(jwtService).generateToken(any(UserDetailsImpl.class));
  }

  @Test
  public void testRegister_emailAlreadyInUse() {
    // given
    User existingUser = User.builder().email(request.getEmail()).password("existingPassword").build();
    when(userRepository.findByEmail(request.getEmail().toLowerCase())).thenReturn(Optional.of(existingUser));

    // then
    AuthenticationException thrown = assertThrows(AuthenticationException.class, () -> {
      // when
      authenticationService.register(request);
    });

    assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    assertEquals("Email is already in use", thrown.getMessage());
  }

  @Test
  public void testLogin() {
    // given
    Authentication mockAuthentication = Mockito.mock(Authentication.class);
    UsernamePasswordAuthenticationToken credentials =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    // when
    when(authenticationManager.authenticate(credentials)).thenReturn(mockAuthentication);
    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
    when(jwtService.generateToken(userDetailsCaptor.capture())).thenReturn("token");

    // then
    AuthenticationResponseDTO response = authenticationService.login(request);
    UserDetailsImpl capturedUserDetails = userDetailsCaptor.getValue();

    assertEquals(user.getId(), response.getUserId(), "User ID should match");
    assertEquals(user.getEmail(), response.getEmail(), "Email should match");
    assertEquals("token", response.getAccessToken(), "Access token should match");
    assertEquals(user.getEmail(), capturedUserDetails.getUsername(), "Usernames should match");

    // verify interactions
    verify(authenticationManager).authenticate(credentials);
    verify(userRepository).findByEmail(request.getEmail());
    verify(jwtService).generateToken(any(UserDetailsImpl.class));
  }

  @Test
  public void testLogin_badCredentials() {
    // given
    request.setPassword("wrong password");

    UsernamePasswordAuthenticationToken credentials =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    // when
    when(authenticationManager.authenticate(credentials))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    // then
    AuthenticationException thrown = assertThrows(
        AuthenticationException.class,
        () -> authenticationService.login(request),
        "Expected login to throw AuthenticationException for bad credentials"
    );

    assertEquals("Invalid email or password", thrown.getMessage(), "Exception message should match");
    assertEquals(HttpStatus.UNAUTHORIZED, thrown.getHttpStatus(), "HTTP status should be 401 Unauthorized");
  }
}
