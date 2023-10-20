package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;

import java.util.List;

public interface BoardService {
  BoardDTO addBoardToUser(String userId, BoardDTO boardDTO);

  List<BoardDTO> getAllUserBoards(String userId);

  List<ColumnDTO> getAllColumnsForBoard(String boardId);

  BoardDTO getBoardById(String id);

  BoardDTO updateBoardName(String id, BoardDTO boardDTO);

  void deleteBoard(String id);
}
