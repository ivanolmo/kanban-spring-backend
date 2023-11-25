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
        .createdAt(subtask.getCreatedAt())
        .updatedAt(subtask.getUpdatedAt())
        .build();
  }
}
