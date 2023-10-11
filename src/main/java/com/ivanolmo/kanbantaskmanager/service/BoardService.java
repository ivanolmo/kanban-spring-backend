package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;

import java.util.List;

public interface BoardService {
  BoardDTO createBoard(BoardDTO board, Long userId);
  List<BoardDTO> getAllUserBoards(Long userId);
  BoardDTO getBoardById(Long id);
  BoardDTO updateBoardName(Long id, BoardDTO boardDetails);
  BoardDTO deleteBoard(Long id);
}
