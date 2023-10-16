package com.ivanolmo.kanbantaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
  private Long id;

  @NotBlank(message = "Task title cannot be blank")
  @Size(min = 3, max = 50, message = "Task title should be between 3 and 50 characters")
  private String title;

  @NotBlank(message = "Task description cannot be blank")
  @Size(min = 3, max = 255, message = "Task description should be between 3 and 255 characters")
  private String description;

  private List<SubtaskDTO> subtasks;
}
