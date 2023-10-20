package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import org.springframework.stereotype.Component;

@Component
public class SubtaskMapper {
  public SubtaskDTO toDTO(Subtask subtask) {
    if (subtask == null) {
      return null;
    }

    return SubtaskDTO.builder()
        .id(subtask.getId())
        .title(subtask.getTitle())
        .completed(subtask.getCompleted())
        .build();
  }

  public Subtask toEntity(SubtaskDTO subtaskDTO) {
    if (subtaskDTO == null) {
      return null;
    }

    return new Subtask.Builder()
        .id(subtaskDTO.getId())
        .title(subtaskDTO.getTitle())
        .completed(subtaskDTO.getCompleted())
        .build();
  }
}
