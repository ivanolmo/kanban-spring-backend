package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardAlreadyExistsException;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardCreationFailedException;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardNotFoundException;
import com.ivanolmo.kanbantaskmanager.exception.user.UserNotFoundException;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BoardServiceImpl implements BoardService {
  private final BoardRepository boardRepository;
  private final UserRepository userRepository;

  public BoardServiceImpl(BoardRepository boardRepository, UserRepository userRepository) {
    this.boardRepository = boardRepository;
    this.userRepository = userRepository;
  }

  // create board
  public Board createBoard(Board board) {
    // check if board already exists
    // this check won't work until user repo/service is implemented
//    Board existingBoard = boardRepository.findBoardByBoardNameAndUserId(board.getBoardName(),
//        board.getUser().getId());
//    if (existingBoard != null) {
//      throw new BoardAlreadyExistsException("A board with this name already exists.");
//    }

    try {
      return boardRepository.save(board);
    } catch (Exception e) {
      throw new BoardCreationFailedException("Failed to create the board.");
    }
  }

  // get all boards for a user
  public List<Board> getAllUserBoards(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException("User does not exist.");
    }

    Optional<List<Board>> optionalBoards = boardRepository.findByUserId(userId);

    return optionalBoards.orElseThrow(() -> new BoardNotFoundException("No boards found for this " +
        "user."));
  }

  // get board by id
  public Board getBoardById(Long id) {
    return boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException("Board not " +
        "found."));
  }

  // update board
  public Board updateBoardName(Long id, Board boardDetails) {
    // get board by id or else throw exception
    Board board = getBoardById(id);

    // get user
    Long userId = board.getUser().getId();

    // check if the new name is the same as any existing board name for this user
    // if match is found throw exception
    if (boardRepository.existsByBoardNameAndUserIdAndIdNot(boardDetails.getBoardName(),
        userId, id)) {
      throw new BoardAlreadyExistsException("A board with that name already exists.");
    }

    // perform update
    board.setBoardName(boardDetails.getBoardName());
    return boardRepository.save(board);
  }

  // delete board
  public void deleteBoard(Long id) {
    // get board by id or else throw exception
    Board board = getBoardById(id);

    // perform delete
    boardRepository.delete(board);
  }
}
