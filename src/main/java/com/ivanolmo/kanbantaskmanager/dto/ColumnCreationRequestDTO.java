package com.ivanolmo.kanbantaskmanager.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnCreationRequestDTO {
  @Valid
  private ColumnDTO column;
  private Long boardId;
}
