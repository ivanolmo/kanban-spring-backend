package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TaskMapper {
  private final SubtaskMapper subtaskMapper;

  public TaskMapper(SubtaskMapper subtaskMapper) {
    this.subtaskMapper = subtaskMapper;
  }

  public TaskDTO toDTO(Task task) {
    if (task == null) {
      return null;
    }

    List<SubtaskDTO> subtasks = Optional.ofNullable(task.getSubtasks())
        .orElse(Collections.emptyList())
        .stream()
        .map(subtaskMapper::toDTO)
        .toList();

    return TaskDTO.builder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .createdAt(task.getCreatedAt())
        .updatedAt(task.getUpdatedAt())
        .subtasks(subtasks)
        .columnId(task.getColumn().getId())
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
        .subtasks(taskDTO.getSubtasks())
        .build();
  }
}
