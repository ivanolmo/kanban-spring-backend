package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.board.*;
import com.ivanolmo.kanbantaskmanager.exception.user.UserNotFoundException;
import com.ivanolmo.kanbantaskmanager.mapper.BoardMapper;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BoardServiceImpl implements BoardService {
  private final BoardRepository boardRepository;
  private final ColumnRepository columnRepository;
  private final UserRepository userRepository;
  private final BoardMapper boardMapper;
  private final ColumnMapper columnMapper;

  public BoardServiceImpl(BoardRepository boardRepository,
                          ColumnRepository columnRepository,
                          UserRepository userRepository,
                          BoardMapper boardMapper,
                          ColumnMapper columnMapper) {
    this.boardRepository = boardRepository;
    this.columnRepository = columnRepository;
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
        boardRepository.findByUserId(userId)
            .orElseThrow(() -> new BoardNotFoundException("No boards found for this user."));

    // map boards to DTOs and return as list
    return boards.stream()
        .map(boardMapper::toDTO)
        .toList();
  }

  // get board by id
  @Transactional(readOnly = true)
  public BoardDTO getBoardById(Long id) {
    // get board by id or else throw exception
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new BoardNotFoundException("Board not found."));

    // map board to DTO and return
    return boardMapper.toDTO(board);
  }

  @Transactional(readOnly = true)
  public List<ColumnDTO> getAllColumnsForBoard(Long boardId) {
    // find board by id or else throw exception
    if (!boardRepository.existsById(boardId)) {
      throw new BoardNotFoundException("Board not found.");
    }

    // get board columns
    List<Column> columns = columnRepository.findAllByBoardId(boardId);

    // map columns to DTOs and return as list
    return columns.stream()
        .map(columnMapper::toDTO)
        .toList();
  }

  @Transactional
  public BoardDTO addBoardToUser(Long userId, BoardDTO boardDTO) {
    // get user, throw error if not found
    User user =
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found."));

    // if new board name already exists for this user, throw error
    boardRepository.findByNameAndUserId(boardDTO.getName(), userId)
        .ifPresent(board -> {
          throw new BoardAlreadyExistsException("A board with this name already exists.");
        });

    // convert the BoardDTO to a Board entity and set user
    Board board = boardMapper.toEntity(boardDTO);
    board.setUser(user);

    // save and return dto, throw error if exception
    try {
      board = boardRepository.save(board);
      return boardMapper.toDTO(board);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new BoardCreationException("Failed to create the board.", e);
    }
  }

  // update board
  @Transactional
  public BoardDTO updateBoardName(Long id, BoardDTO boardDTO) {
    // get board by id or else throw exception
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new BoardNotFoundException("Board not found."));

    // get user that this board belongs to
    Long userId = board.getUser().getId();

    // if board name already exists for this user, throw error
    boardRepository.findByNameAndUserId(boardDTO.getName(), userId)
        .ifPresent(existingBoard -> {
          throw new BoardAlreadyExistsException("A board with that name already exists.");
        });

    // perform update and return dto
    try {
      board.setName(boardDTO.getName());
      Board updatedBoard = boardRepository.save(board);
      return boardMapper.toDTO(updatedBoard);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new BoardUpdateException("There was an error updating this board.", e);
    }
  }

  // delete board
  @Transactional
  public void deleteBoard(Long id) {
    // delete board or throw error if board not found
    try {
      boardRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new BoardNotFoundException("Board not found.");
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new BoardDeleteException("There was an error deleting this board.", e);
    }
  }
}
