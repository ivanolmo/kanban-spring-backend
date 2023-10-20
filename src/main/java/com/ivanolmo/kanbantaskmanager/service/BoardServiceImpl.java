package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.BoardMapper;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
  public List<BoardDTO> getAllUserBoards(String userId) {
    // find user by id or else throw exception
    if (!userRepository.existsById(userId)) {
      throw new EntityOperationException("User", "read", HttpStatus.NOT_FOUND);
    }

    // get users boards
    List<Board> boards = boardRepository.findByUserId(userId).orElse(Collections.emptyList());

    // map boards to DTOs and return as list
    return boards.stream()
        .map(boardMapper::toDTO)
        .toList();
  }

  // get board by id
  @Transactional(readOnly = true)
  public BoardDTO getBoardById(String id) {
    // get board by id or else throw exception
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // map board to DTO and return
    return boardMapper.toDTO(board);
  }

  @Transactional(readOnly = true)
  public List<ColumnDTO> getAllColumnsForBoard(String boardId) {
    // find board by id or else throw exception
    if (!boardRepository.existsById(boardId)) {
      throw new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND);
    }

    // get board columns
    List<Column> columns = columnRepository.findAllByBoardId(boardId).orElse(Collections.emptyList());


    // map columns to DTOs and return as list
    return columns.stream()
        .map(columnMapper::toDTO)
        .toList();
  }

  @Transactional
  public BoardDTO addBoardToUser(String userId, BoardDTO boardDTO) {
    // get user, throw error if not found
    User user =
        userRepository.findById(userId)
            .orElseThrow(() -> new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // if new board name already exists for this user, throw error
    boardRepository.findByNameAndUserId(boardDTO.getName(), userId)
        .ifPresent(board -> {
          throw new EntityOperationException("A board with that name already exists.",
              HttpStatus.CONFLICT);
        });

    // convert the BoardDTO to a Board entity and set user
    Board board = boardMapper.toEntity(boardDTO);
    board.setUser(user);

    // save and return dto, throw error if exception
    try {
      board = boardRepository.save(board);
      return boardMapper.toDTO(board);
    } catch (Exception e) {
      log.error("An error occurred while adding board '{}' to user '{}': {}",
          boardDTO.getName(), userId, e.getMessage());
      throw new EntityOperationException("Board", "create", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // update board
  @Transactional
  public BoardDTO updateBoardName(String id, BoardDTO boardDTO) {
    // get board by id or else throw exception
    Board board = boardRepository.findById(id)
        .orElseThrow(() -> new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // get user that this board belongs to
    String userId = board.getUser().getId();

    // if board name already exists for this user, throw error
    boardRepository.findByNameAndUserId(boardDTO.getName(), userId)
        .ifPresent(existingBoard -> {
          throw new EntityOperationException("A board with that name already exists.",
              HttpStatus.CONFLICT);
        });

    // perform update and return dto
    try {
      board.setName(boardDTO.getName());
      Board updatedBoard = boardRepository.save(board);
      return boardMapper.toDTO(updatedBoard);
    } catch (Exception e) {
      log.error("An error occurred while updating board '{}': {}",
          boardDTO.getName(), e.getMessage());
      throw new EntityOperationException("Board", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // delete board
  @Transactional
  public void deleteBoard(String id) {
    // delete board or throw error if board not found
    try {
      boardRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.error("An error occurred while deleting board id {}: {}",
          id, e.getMessage());
      throw new EntityOperationException("Board", "delete", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      log.error("An error occurred while deleting board id {}: {}",
          id, e.getMessage());
      throw new EntityOperationException("Board", "delete", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
