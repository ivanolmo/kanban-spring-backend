package com.ivanolmo.kanbantaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardCreationRequestDTO {
  private BoardDTO board;
  private Long userId;
}
