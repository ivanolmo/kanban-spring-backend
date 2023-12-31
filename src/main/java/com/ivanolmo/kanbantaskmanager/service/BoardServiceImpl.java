package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.BoardInfo;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.BoardMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.util.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
  private final BoardRepository boardRepository;
  private final BoardMapper boardMapper;
  private final ColumnService columnService;
  private final UserHelper userHelper;

  // get all boards for a user
  @Transactional(readOnly = true)
  public List<BoardDTO> getAllUserBoards() {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get users boards
    List<Board> boards =
        boardRepository.findAllByUserId(user.getId()).orElse(Collections.emptyList());

    // map boards to DTOs and return as list
    return boards.stream()
        .map(boardMapper::toDTO)
        .toList();
  }

  @Transactional
  public BoardDTO addBoardToUser(BoardDTO boardDTO) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // check if new board name already exists for this user
    boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())
        .ifPresent(board -> {
          throw new EntityOperationException(
              "A board with that name already exists", HttpStatus.CONFLICT);
        });

    // convert the BoardDTO to a Board entity and set user
    Board board = boardMapper.toEntity(boardDTO);
    board.setUser(user);

    // save and return dto
    try {
      board = boardRepository.save(board);
      return boardMapper.toDTO(board);
    } catch (Exception e) {
      log.error("An error occurred while adding board '{}' to user '{}': {}",
          board.getName(), user.getId(), e.getMessage());
      throw new EntityOperationException("Board", "create", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // update board
  @Transactional
  public BoardDTO updateBoard(String id, BoardDTO boardDTO) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get board by id
    Board board = boardRepository.findByIdAndUserId(id, user.getId())
        .orElseThrow(() -> new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // check if board exists and belongs to the current user
    if (!board.getUser().getId().equals(user.getId())) {
      throw new EntityOperationException("You do not have permission to update this board",
          HttpStatus.FORBIDDEN);
    }

    // check if board name already exists but exclude current board
    boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())
        .filter(existingBoard -> !existingBoard.getId().equals(id))
        .ifPresent(existingBoard -> {
          throw new EntityOperationException("A board with that name already exists",
              HttpStatus.CONFLICT);
        });

    // update board name
    board.setName(boardDTO.getName());

    // update columns and get the updated column DTOs
    List<ColumnDTO> updatedColumnDTOs = columnService.updateColumns(id, boardDTO.getColumns());

    try {
      // save the board after column updates
      Board updatedBoard = boardRepository.save(board);

      // create and return the updated BoardDTO
      BoardDTO updatedBoardDTO = boardMapper.toDTO(updatedBoard);

      // Set updated columns in DTO
      updatedBoardDTO.setColumns(updatedColumnDTOs);
      return updatedBoardDTO;
    } catch (Exception e) {
      log.error("An error occurred while updating board '{}': {}",
          board.getName(), e.getMessage());
      throw new EntityOperationException("Board", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // delete board
  @Transactional
  public void deleteBoard(String id) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get board and user info
    BoardInfo boardInfo = boardRepository.findBoardInfoById(id)
        .orElseThrow(() -> new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // check if board exists and belongs to the current user
    if (!boardInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to delete this board", HttpStatus.FORBIDDEN);
    }

    // delete board
    try {
      boardRepository.deleteById(id);
    } catch (Exception e) {
      log.error("An error occurred while deleting board id {}: {}", id, e.getMessage());
      throw new EntityOperationException("Board", "delete", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
