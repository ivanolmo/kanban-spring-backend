package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;

import java.util.List;

public interface BoardColumnService {
  BoardColumn createBoardColumn(BoardColumn boardColumn);
  List<BoardColumn> getAllUserBoardColumns(Long userId);
  BoardColumn getBoardColumnById(Long id);
  BoardColumn updateBoardColumn(Long id, BoardColumn boardColumnDetails);
  void deleteBoardColumn(Long id);
}
