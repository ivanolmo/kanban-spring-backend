package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;

public interface ColumnService {
  ColumnDTO addColumnToBoard(ColumnDTO columnDTO, Long boardId);

  ColumnDTO updateColumnName(Long id, ColumnDTO columnDTO);

  ColumnDTO deleteColumn(Long id);
}
