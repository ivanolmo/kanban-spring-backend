package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardNotFoundException;
import com.ivanolmo.kanbantaskmanager.exception.column.*;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    Optional<Board> boardOptional = boardRepository.findById(boardId);
    if (boardOptional.isEmpty()) {
      throw new BoardNotFoundException("Board not found.");
    }

    // get column from optional, convert the ColumnDTO to a Column entity and set to board
    Board board = boardOptional.get();
    Column column = columnMapper.toEntity(columnDTO);
    column.setBoard(board);

    try {
      column = columnRepository.save(column);
      return columnMapper.toDTO(column);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnCreationFailedException("Failed to create the column.", e);
    }
  }

  // update column
  @Transactional
  public ColumnDTO updateColumnName(Long id, ColumnDTO columnDTO) {
    // get column by id or else throw exception
    Optional<Column> columnToUpdateOptional = columnRepository.findById(id);

    if (columnToUpdateOptional.isEmpty()) {
      throw new ColumnNotFoundException("Column not found.");
    }

    // get column from opt
    Column column = columnToUpdateOptional.get();

    // get board that this column belongs to
    Long boardId = column.getBoard().getId();

    // check if the new column name is the same as any existing column name for this board
    // if match is found throw exception
    Optional<Column> existingColumnNameOptional =
        columnRepository.findByNameAndBoardId(columnDTO.getName(),
            boardId);

    if (existingColumnNameOptional.isPresent()) {
      throw new ColumnAlreadyExistsException("A column with that name already exists.");
    }

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
    // get column by id or else throw exception
    Optional<Column> columnOptional = columnRepository.findById(id);
    if (columnOptional.isEmpty()) {
      throw new ColumnNotFoundException("Column not found.");
    }

    // capture the column to be deleted and delete
    try {
      columnRepository.delete(columnOptional.get());
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnDeleteException("There was an error deleting this column.", e);
    }
  }
}
