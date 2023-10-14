package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;

public interface BoardColumnService {
  BoardColumnDTO addColumnToBoard(BoardColumnDTO boardColumnDTO, Long boardId);
  BoardColumn getBoardColumnById(Long id);
  BoardColumnDTO updateBoardColumnName(Long id, BoardColumnDTO boardColumnDTO);
  BoardColumnDTO deleteBoardColumn(Long id);
}
