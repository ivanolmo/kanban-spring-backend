package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardMapper {
  public BoardDTO toDTO(Board board) {
    if (board == null) {
      return null;
    }

    List<BoardColumnDTO> boardColumns = board.getBoardColumns().stream()
        .map(boardColumn -> new BoardColumnDTO(
            boardColumn.getId(),
            boardColumn.getColumnName()))
        .toList();

    return BoardDTO.builder()
        .id(board.getId())
        .boardName(board.getBoardName())
        .columns(boardColumns)
        .build();
  }

  public Board toEntity(BoardDTO boardDTO) {
    if (boardDTO == null) {
      return null;
    }

    return new Board.Builder()
        .id(boardDTO.getId())
        .boardName(boardDTO.getBoardName())
        .boardColumns(boardDTO.getColumns())
        .build();
  }
}
