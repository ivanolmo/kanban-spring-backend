package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BoardMapper {
  private final TaskMapper taskMapper;

  public BoardMapper(TaskMapper taskMapper) {
    this.taskMapper = taskMapper;
  }

  public BoardDTO toDTO(Board board) {
    if (board == null) {
      return null;
    }

    List<ColumnDTO> columns = board.getColumns().stream()
        .map(column -> {
          List<TaskDTO> taskDTOs = column.getTasks().stream()
              .map(taskMapper::toDTO)
              .collect(Collectors.toList());

          return new ColumnDTO(
              column.getId(),
              column.getName(),
              taskDTOs
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
