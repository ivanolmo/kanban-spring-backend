package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;

public interface ColumnService {
  ColumnDTO addColumnToBoard(String boardId, ColumnDTO columnDTO);

  ColumnDTO updateColumnName(String id, ColumnDTO columnDTO);

  void deleteColumn(String id);
}
