package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BoardMapper {
  private final TaskMapper taskMapper;
  private final SubtaskMapper subtaskMapper;

  public BoardMapper(TaskMapper taskMapper, SubtaskMapper subtaskMapper) {
    this.taskMapper = taskMapper;
    this.subtaskMapper = subtaskMapper;
  }

  public BoardDTO toDTO(Board board) {
    if (board == null) {
      return null;
    }

    List<ColumnDTO> columns = board.getColumns().stream()
        .map(column -> {
          List<TaskDTO> tasks = Optional.ofNullable(column.getTasks())
              .orElse(Collections.emptyList())
              .stream()
              .map(task -> {
                TaskDTO taskDTO = taskMapper.toDTO(task);

                List<SubtaskDTO> subtasks = Optional.ofNullable(task.getSubtasks())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(subtaskMapper::toDTO)
                    .toList();

                taskDTO.setSubtasks(subtasks);
                return taskDTO;
              }).toList();
          return new ColumnDTO(
              column.getId(),
              column.getName(),
              tasks
          );
        })
        .toList();

    return BoardDTO.builder()
        .id(board.getId())
        .name(board.getName())
        .columns(columns)
        .build();
  }

  public Board toEntity(BoardDTO boardDTO) {
    if (boardDTO == null) {
      return null;
    }

    return new Board.Builder()
        .id(boardDTO.getId())
        .name(boardDTO.getName())
        .columns(boardDTO.getColumns())
        .build();
  }
}
