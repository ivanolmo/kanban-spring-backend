package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardInfo;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnInfo;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.util.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColumnServiceImpl implements ColumnService {
  private final ColumnRepository columnRepository;
  private final BoardRepository boardRepository;
  private final ColumnMapper columnMapper;
  private final UserHelper userHelper;

  // create board column
  @Transactional
  public ColumnDTO addColumnToBoard(String boardId, ColumnDTO columnDTO) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get board and user info
    BoardInfo boardInfo = boardRepository.findBoardInfoById(boardId)
        .orElseThrow(() -> new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // check that column -> board relation and user id matches
    if (!boardInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to add a column to this board", HttpStatus.FORBIDDEN);
    }

    Board board = boardInfo.getBoard();

    // check if new column name matches an existing name
    columnRepository.findByNameAndBoardId(columnDTO.getName(), boardId)
        .ifPresent(existingColumn -> {
          throw new EntityOperationException("A column with that name already exists",
              HttpStatus.CONFLICT);
        });

    // convert the ColumnDTO to a Column entity and set to board
    Column column = columnMapper.toEntity(columnDTO);
    column.setBoard(board);

    // save and return dto, throw error if exception
    try {
      column = columnRepository.save(column);
      return columnMapper.toDTO(column);
    } catch (Exception e) {
      log.error("An error occurred while adding column '{}' to board '{}': {}",
          columnDTO.getName(), board.getName(), e.getMessage());
      throw new EntityOperationException("Column", "create", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // update column
  @Transactional
  public ColumnDTO updateColumnName(String id, ColumnDTO columnDTO) {
    // Get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get column and user info
    ColumnInfo columnInfo = columnRepository.findColumnInfoById(id)
        .orElseThrow(() -> new EntityOperationException("Column", "read", HttpStatus.NOT_FOUND));

    // check if column info and user id matches
    if (!columnInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to update a column in this board", HttpStatus.FORBIDDEN);
    }

    Column column = columnInfo.getColumn();

    // check if updated name matches an existing name
    columnRepository.findByNameAndBoardId(columnDTO.getName(), column.getBoard().getId())
        .ifPresent(existingColumn -> {
          throw new EntityOperationException(
              "A column with that name already exists", HttpStatus.CONFLICT);
        });

    // update column name and return DTO
    try {
      column.setName(columnDTO.getName());
      Column updatedColumn = columnRepository.save(column);
      return columnMapper.toDTO(updatedColumn);
    } catch (Exception e) {
      log.error(
          "An error occurred while updating column '{}': {}", columnDTO.getName(), e.getMessage());
      throw new EntityOperationException("Column", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // delete column
  @Transactional
  public void deleteColumn(String id) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get column and user info
    ColumnInfo columnInfo = columnRepository.findColumnInfoById(id)
        .orElseThrow(() -> new EntityOperationException("Column", "read", HttpStatus.NOT_FOUND));

    // check if column info and user id matches
    if (!columnInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to delete a column from this board", HttpStatus.FORBIDDEN);
    }

    // delete column
    try {
      columnRepository.deleteById(id);
    } catch (Exception e) {
      log.error("An error occurred while deleting column id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Column", "delete", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
