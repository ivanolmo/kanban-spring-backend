package com.ivanolmo.kanbantaskmanager.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnCreationRequest {
  private ColumnDTO column;
  private Long boardId;
}
