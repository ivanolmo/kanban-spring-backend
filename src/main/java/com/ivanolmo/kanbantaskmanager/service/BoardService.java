package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardCreationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;

import java.util.List;

public interface BoardService {
  List<BoardDTO> getAllUserBoards();

  BoardDTO getBoardById(String id);

  BoardDTO addBoardToUser(BoardDTO boardDTO);

  BoardDTO updateBoardName(String id, String newName);

  void deleteBoard(String id);
}
