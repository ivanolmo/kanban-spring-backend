package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;

import java.util.List;

public interface BoardService {
  BoardDTO createBoard(BoardDTO board, Long userId);

  List<BoardDTO> getAllUserBoards(Long userId);

  List<ColumnDTO> getAllColumnsForBoard(Long boardId);

  BoardDTO getBoardById(Long id);

  BoardDTO updateBoardName(Long id, BoardDTO boardDTO);

  void deleteBoard(Long id);
}
