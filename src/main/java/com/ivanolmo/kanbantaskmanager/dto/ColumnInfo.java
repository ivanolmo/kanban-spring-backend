package com.ivanolmo.kanbantaskmanager.dto;

import com.ivanolmo.kanbantaskmanager.entity.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColumnInfo {
  private String columnId;
  private String userId;
  private Column column;
}
