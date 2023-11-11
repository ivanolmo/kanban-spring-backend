package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;

import java.util.List;

public interface ColumnService {
  List<ColumnDTO> updateColumns(String boardId, List<ColumnDTO> columnDTOs);
}
