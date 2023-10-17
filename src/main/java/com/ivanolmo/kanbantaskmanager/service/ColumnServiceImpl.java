package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardNotFoundException;
import com.ivanolmo.kanbantaskmanager.exception.column.*;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
  public ColumnDTO addColumnToBoard(Long boardId, ColumnDTO columnDTO) {
    // get board, throw error if not found
    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new BoardNotFoundException("Board not found."));

    // if new column name already exists for this board, throw error
    columnRepository.findByNameAndBoardId(columnDTO.getName(), boardId)
        .ifPresent(existingColumn -> {
          throw new ColumnAlreadyExistsException("A column with that name already exists.");
        });

    // convert the ColumnDTO to a Column entity and set to board
    Column column = columnMapper.toEntity(columnDTO);
    column.setBoard(board);

    // save and return dto, throw error if exception
    try {
      column = columnRepository.save(column);
      return columnMapper.toDTO(column);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnCreationException("Failed to create the column.", e);
    }
  }

  // update column
  @Transactional
  public ColumnDTO updateColumnName(Long id, ColumnDTO columnDTO) {
    // get column by id or else throw exception
    Column column = columnRepository.findById(id)
        .orElseThrow(() -> new ColumnNotFoundException("Column not found."));

    // get board that this column belongs to
    Long boardId = column.getBoard().getId();

    // if column name already exists for this board, throw error
    columnRepository.findByNameAndBoardId(columnDTO.getName(), boardId)
        .ifPresent(existingColumn -> {
          throw new ColumnAlreadyExistsException("A column with that name already exists.");
        });

    // perform update and return dto
    try {
      column.setName(columnDTO.getName());
      Column updatedColumn = columnRepository.save(column);
      return columnMapper.toDTO(updatedColumn);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnUpdateException("There was an error updating this column.", e);
    }
  }

  // delete column
  @Transactional
  public void deleteColumn(Long id) {
    // delete column or throw error if column not found
    try {
      columnRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnNotFoundException("Column not found.");
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnDeleteException("There was an error deleting this column.", e);
    }
  }
}
