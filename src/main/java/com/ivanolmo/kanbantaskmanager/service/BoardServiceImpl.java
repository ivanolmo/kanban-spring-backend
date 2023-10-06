package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {
  private final BoardRepository boardRepository;

  public BoardServiceImpl(BoardRepository boardRepository) {
    this.boardRepository = boardRepository;
  }

  // create board
  public Board createBoard(Board board) {
    return boardRepository.save(board);
  }

  // get all boards for a user
  public List<Board> getAllUserBoards(Long userId) {
    return boardRepository.findByUserId(userId);
  }

  // get board by id
  public Board getBoardById(Long id) {
    return boardRepository.findById(id).orElse(null);
  }

  // update board
  public Board updateBoard(Long id, Board boardDetails) {
    Board board = getBoardById(id);

    if (board != null) {
      board.setBoardName(boardDetails.getBoardName());
      return boardRepository.save(board);
    }
    return null;
  }

  // delete board
  public void deleteBoard(Long id) {
    Board board = getBoardById(id);

    if (board != null) {
      boardRepository.delete(board);
    }
  }
}
