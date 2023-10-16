package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;

public interface ColumnService {
  ColumnDTO addColumnToBoard(Long boardId, ColumnDTO columnDTO);

  ColumnDTO updateColumnName(Long id, ColumnDTO columnDTO);

  ColumnDTO deleteColumn(Long id);
}
