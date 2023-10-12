package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BoardMapper {
  public BoardDTO toDTO(Board board) {
    List<ColumnDTO> columns = board.getBoardColumns().stream()
        .map(boardColumn -> new ColumnDTO(boardColumn.getColumnName()))
        .toList();
    return new BoardDTO(board.getId(), board.getBoardName(), columns);
  }

  public Board toEntity(BoardDTO boardDTO) {
    Board board = new Board();

    board.setId(boardDTO.getId());
    board.setBoardName(boardDTO.getBoardName());
    List<BoardColumn> columns = boardDTO.getColumns().stream()
        .map(columnDTO -> {
          BoardColumn column = new BoardColumn();
          column.setColumnName(columnDTO.getColumnName());
          column.setBoard(board);
          return column;
        })
        .toList();
    board.setBoardColumns(columns);

    return board;
  }
}
