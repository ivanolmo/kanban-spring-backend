package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;

import java.util.List;

public interface BoardService {
  List<BoardDTO> getAllUserBoards();

  BoardDTO addBoardToUser(BoardDTO boardDTO);

  BoardDTO updateBoard(String id, BoardDTO boardDTO);

  void deleteBoard(String id);
}
