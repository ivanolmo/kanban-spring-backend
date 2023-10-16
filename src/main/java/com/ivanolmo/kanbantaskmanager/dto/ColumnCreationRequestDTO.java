package com.ivanolmo.kanbantaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnCreationRequestDTO {
  private ColumnDTO column;
  private Long boardId;
}
