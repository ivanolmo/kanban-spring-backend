package com.ivanolmo.kanbantaskmanager.dto;

import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubtaskInfo {
  private String subtaskId;
  private String userId;
  private Subtask subtask;
}
