package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Board;

import java.util.List;

public interface BoardService {
  Board createBoard(Board board);
  List<Board> getAllUserBoards(Long userId);
  Board getBoardById(Long id);
  Board updateBoard(Long id, Board boardDetails);
  void deleteBoard(Long id);
}
