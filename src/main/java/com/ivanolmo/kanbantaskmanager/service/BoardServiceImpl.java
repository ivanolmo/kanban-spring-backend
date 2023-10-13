package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.exception.board.*;
import com.ivanolmo.kanbantaskmanager.exception.user.UserNotFoundException;
import com.ivanolmo.kanbantaskmanager.mapper.BoardMapper;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoardServiceImpl implements BoardService {
  private final BoardRepository boardRepository;
  private final BoardColumnRepository boardColumnRepository;
  private final UserRepository userRepository;
  private final BoardMapper boardMapper;
  private final ColumnMapper columnMapper;

  public BoardServiceImpl(BoardRepository boardRepository,
                          BoardColumnRepository boardColumnRepository,
                          UserRepository userRepository,
                          BoardMapper boardMapper,
                          ColumnMapper columnMapper) {
    this.boardRepository = boardRepository;
    this.boardColumnRepository = boardColumnRepository;
    this.userRepository = userRepository;
    this.boardMapper = boardMapper;
    this.columnMapper = columnMapper;
  }

  // get all boards for a user
  @Transactional(readOnly = true)
  public List<BoardDTO> getAllUserBoards(Long userId) {
    // find user by id or else throw exception
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException("User does not exist.");
    }

    // get users boards
    List<Board> boards =
        boardRepository.findByUserId(userId).orElseThrow(() -> new BoardNotFoundException("No " +
            "boards found for this user."));

    // map boards to DTOs and return as list
    return boards.stream()
        .map(boardMapper::toDTO)
        .collect(Collectors.toList());
  }

  // get board by id
  @Transactional(readOnly = true)
  public BoardDTO getBoardById(Long id) {
    // get board by id or else throw exception
    Optional<Board> optBoard = boardRepository.findById(id);

    if (optBoard.isEmpty()) {
      throw new BoardNotFoundException("Board not found.");
    }

    // map board to DTO and return
    return boardMapper.toDTO(optBoard.get());
  }

  @Transactional(readOnly = true)
  public List<BoardColumnDTO> getAllColumnsForBoard(Long boardId) {
    // find board by id or else throw exception
    if (!boardRepository.existsById(boardId)) {
      throw new BoardNotFoundException("Board not found.");
    }

    // get board columns
    List<BoardColumn> boardColumns = boardColumnRepository.findAllColumnsByBoardId(boardId);

    // map columns to DTOs and return as list
    return boardColumns.stream()
        .map(columnMapper::toDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public BoardDTO createBoard(BoardDTO boardDTO, Long userId) {
    // check if board already exists
    // get an Optional using a custom query and check if it is present. if present, throw error
    Optional<Board> existingBoardOpt =
        boardRepository.findBoardByBoardNameAndUserId(boardDTO.getBoardName(),
            userId);
    if (existingBoardOpt.isPresent()) {
      throw new BoardAlreadyExistsException("A board with this name already exists.");
    }

    // get the user from the repository
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not " +
            "found."));

    // convert the BoardDTO to a Board entity and set user
    Board board = boardMapper.toEntity(boardDTO);
    board.setUser(user);

    try {
      board = boardRepository.save(board);
      return boardMapper.toDTO(board);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new BoardCreationFailedException("Failed to create the board.", e);
    }
  }

  // update board
  @Transactional
  public BoardDTO updateBoardName(Long id, BoardDTO boardDetailsDTO) {
    // get board by id or else throw exception
    Optional<Board> optBoard = boardRepository.findById(id);

    if (optBoard.isEmpty()) {
      throw new BoardNotFoundException("Board not found.");
    }

    Board board = optBoard.get();

    // get user
    Long userId = Optional.ofNullable(board.getUser())
        .map(User::getId)
        .orElseThrow(() -> new UserNotFoundException("User not found for this board."));

    // check if the new name is the same as any existing board name for this user
    // if match is found throw exception
    if (boardRepository.existsByBoardNameAndUserIdAndIdNot(boardDetailsDTO.getBoardName(),
        userId, id)) {
      throw new BoardAlreadyExistsException("A board with that name already exists.");
    }

    try {
      // perform update and return
      board.setBoardName(boardDetailsDTO.getBoardName());
      Board updatedBoard = boardRepository.save(board);
      return boardMapper.toDTO(updatedBoard);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new BoardUpdateException("There was an error updating this board.", e);
    }
  }

  // delete board
  @Transactional
  public BoardDTO deleteBoard(Long id) {
    // get board by id or else throw exception
    Optional<Board> optBoard = boardRepository.findById(id);

    if (optBoard.isEmpty()) {
      throw new BoardNotFoundException("Board not found.");
    }

    try {
      // capture the board to be deleted, delete, and return
      Board board = optBoard.get();
      boardRepository.delete(board);
      return boardMapper.toDTO(board);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new BoardDeleteException("There was an error deleting this board.", e);
    }
  }
}
