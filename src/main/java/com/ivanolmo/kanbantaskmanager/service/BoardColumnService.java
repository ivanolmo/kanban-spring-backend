package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;

public interface BoardColumnService {
  BoardColumnDTO addColumnToBoard(BoardColumnDTO boardColumnDTO, Long boardId);
  BoardColumn getBoardColumnById(Long id);
  BoardColumn updateBoardColumn(Long id, BoardColumn boardColumnDetails);
  void deleteBoardColumn(Long id);
}
