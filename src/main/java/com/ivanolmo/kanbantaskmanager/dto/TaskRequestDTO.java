package com.ivanolmo.kanbantaskmanager.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDTO {
  @Valid
  private TaskDTO task;
  private String columnId;
}
