package com.ivanolmo.kanbantaskmanager.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {
  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("email")
  private String email;

  @JsonProperty("access_token")
  private String accessToken;
}
