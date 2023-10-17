package com.ivanolmo.kanbantaskmanager.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardCreationRequestDTO {
  @Valid
  private BoardDTO board;
  private Long userId;
}
