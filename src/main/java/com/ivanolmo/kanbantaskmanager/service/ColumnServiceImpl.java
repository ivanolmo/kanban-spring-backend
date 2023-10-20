package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ColumnServiceImpl implements ColumnService {
  private final ColumnRepository columnRepository;
  private final BoardRepository boardRepository;
  private final ColumnMapper columnMapper;

  public ColumnServiceImpl(ColumnRepository columnRepository,
                           BoardRepository boardRepository,
                           ColumnMapper columnMapper) {
    this.columnRepository = columnRepository;
    this.boardRepository = boardRepository;
    this.columnMapper = columnMapper;
  }

  // create board column
  @Transactional
  public ColumnDTO addColumnToBoard(String boardId, ColumnDTO columnDTO) {
    // get board, throw error if not found
    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // if new column name already exists for this board, throw error
    columnRepository.findByNameAndBoardId(columnDTO.getName(), boardId)
        .ifPresent(existingColumn -> {
          throw new EntityOperationException("A column with that name already exists.",
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
    // get column by id or else throw exception
    Column column = columnRepository.findById(id)
        .orElseThrow(() -> new EntityOperationException("Column", "read", HttpStatus.NOT_FOUND));

    // get board that this column belongs to
    String boardId = column.getBoard().getId();

    // if column name already exists for this board, throw error
    columnRepository.findByNameAndBoardId(columnDTO.getName(), boardId)
        .ifPresent(existingColumn -> {
          throw new EntityOperationException("A column with that name already exists.",
              HttpStatus.CONFLICT);
        });

    // perform update and return dto
    try {
      column.setName(columnDTO.getName());
      Column updatedColumn = columnRepository.save(column);
      return columnMapper.toDTO(updatedColumn);
    } catch (Exception e) {
      log.error("An error occurred while updating column '{}': {}",
          columnDTO.getName(), e.getMessage());
      throw new EntityOperationException("Column", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // delete column
  @Transactional
  public void deleteColumn(String id) {
    // delete column or throw error if column not found
    try {
      columnRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.error("An error occurred while deleting column id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Column", "delete", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      log.error("An error occurred while deleting column id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Column", "delete", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
