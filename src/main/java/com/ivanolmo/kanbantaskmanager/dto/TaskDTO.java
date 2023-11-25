package com.ivanolmo.kanbantaskmanager.dto;

import com.ivanolmo.kanbantaskmanager.dto.validation.ValidTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidTask
public class TaskDTO {
  private String id;

  private String title;

  private String description;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private List<SubtaskDTO> subtasks;

  private String columnId;
}
