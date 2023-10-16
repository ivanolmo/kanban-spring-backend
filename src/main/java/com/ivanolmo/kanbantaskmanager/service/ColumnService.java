package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;

public interface ColumnService {
  ColumnDTO addColumnToBoard(Long boardId, ColumnDTO columnDTO);

  ColumnDTO updateColumnName(Long id, ColumnDTO columnDTO);

  void deleteColumn(Long id);
}
