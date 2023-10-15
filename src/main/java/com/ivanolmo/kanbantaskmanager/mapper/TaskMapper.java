package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskDTO;
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
}
