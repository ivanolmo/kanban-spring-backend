package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import org.springframework.stereotype.Component;

@Component
public class ColumnMapper {
  public ColumnDTO toDTO(Column column) {
    if (column == null) {
      return null;
    }

    return ColumnDTO.builder()
        .id(column.getId())
        .name(column.getName())
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
