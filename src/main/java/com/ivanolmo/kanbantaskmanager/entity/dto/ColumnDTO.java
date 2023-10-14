package com.ivanolmo.kanbantaskmanager.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColumnDTO {
  private Long id;
  private String name;
}
