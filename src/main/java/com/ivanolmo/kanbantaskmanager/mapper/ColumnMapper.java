package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {
  public BoardColumnDTO toDTO(BoardColumn boardColumn) {
    if (boardColumn == null) {
      return null;
    }

    return BoardColumnDTO.builder()
        .id(boardColumn.getId())
        .columnName(boardColumn.getColumnName())
        .build();
  }

  public BoardColumn toEntity(BoardColumnDTO boardColumnDTO) {
    if (boardColumnDTO == null) {
      return null;
    }

    return new BoardColumn.Builder()
        .id(boardColumnDTO.getId())
        .boardColumnName(boardColumnDTO.getColumnName())
        .build();
  }
}
