package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardMapper {
  public BoardDTO toDTO(Board board) {
    if (board == null) {
      return null;
    }

    List<ColumnDTO> columns = board.getColumns().stream()
        .map(column -> new ColumnDTO(
            column.getId(),
            column.getName()))
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
