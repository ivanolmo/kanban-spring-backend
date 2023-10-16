package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
  public TaskDTO toDTO(Task task) {
    if (task == null) {
      return null;
    }

    return TaskDTO.builder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .build();
  }

  public Task toEntity(TaskDTO taskDTO) {
    if (taskDTO == null) {
      return null;
    }

    return new Task.Builder()
        .id(taskDTO.getId())
        .title(taskDTO.getTitle())
        .description(taskDTO.getDescription())
        .build();
  }
}
