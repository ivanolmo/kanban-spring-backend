package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ColumnMapper {
  private final TaskMapper taskMapper;

  public ColumnMapper(TaskMapper taskMapper) {
    this.taskMapper = taskMapper;
  }

  public ColumnDTO toDTO(Column column) {
    if (column == null) {
      return null;
    }

    List<TaskDTO> tasks = Optional.ofNullable(column.getTasks())
        .orElse(Collections.emptyList())
        .stream()
        .map(taskMapper::toDTO)
        .toList();

    return ColumnDTO.builder()
        .id(column.getId())
        .name(column.getName())
        .tasks(tasks)
        .build();
  }

  public Column toEntity(ColumnDTO columnDTO) {
    if (columnDTO == null) {
      return null;
    }

    return new Column.Builder()
        .id(columnDTO.getId())
        .name(columnDTO.getName())
        .build();
  }
}
