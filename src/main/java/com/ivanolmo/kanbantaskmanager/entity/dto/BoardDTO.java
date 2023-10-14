package com.ivanolmo.kanbantaskmanager.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
  private Long id;
  private String boardName;
  private List<ColumnDTO> columns;
}
