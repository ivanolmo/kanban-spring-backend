package com.ivanolmo.kanbantaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubtaskCreationRequestDTO {
  private SubtaskDTO subtask;
  private Long taskId;
}
