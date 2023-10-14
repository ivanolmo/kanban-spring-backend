package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardNotFoundException;
import com.ivanolmo.kanbantaskmanager.exception.column.*;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class BoardColumnServiceImpl implements BoardColumnService {
  private final BoardColumnRepository boardColumnRepository;
  private final BoardRepository boardRepository;
  private final ColumnMapper columnMapper;

  public BoardColumnServiceImpl(BoardColumnRepository boardColumnRepository,
                                BoardRepository boardRepository,
                                ColumnMapper columnMapper) {
    this.boardColumnRepository = boardColumnRepository;
    this.boardRepository = boardRepository;
    this.columnMapper = columnMapper;
  }

  // get column by id
  @Transactional(readOnly = true)
  public BoardColumn getBoardColumnById(Long id) {
    return boardColumnRepository.findById(id).orElse(null);
  }

  // create board column
  @Transactional
  public BoardColumnDTO addColumnToBoard(BoardColumnDTO boardColumnDTO, Long boardId) {
    // get board, throw error if not found
    Optional<Board> boardOpt = boardRepository.findById(boardId);
    if (boardOpt.isEmpty()) {
      throw new BoardNotFoundException("Board not found.");
    }

    // get column from optional, convert the ColumnDTO to a Column entity and set to board
    Board board = boardOpt.get();
    BoardColumn boardColumn = columnMapper.toEntity(boardColumnDTO);
    boardColumn.setBoard(board);

    try {
      boardColumn = boardColumnRepository.save(boardColumn);
      return columnMapper.toDTO(boardColumn);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnCreationFailedException("Failed to create the column.", e);
    }
  }

  // update column
  @Transactional
  public BoardColumnDTO updateBoardColumnName(Long id, BoardColumnDTO boardColumnDTO) {
    // get column by id or else throw exception
    Optional<BoardColumn> optColumn = boardColumnRepository.findById(id);

    if (optColumn.isEmpty()) {
      throw new ColumnNotFoundException("Column not found.");
    }

    // get column from opt
    BoardColumn boardColumn = optColumn.get();

    // get board that this column belongs to
    Long boardId = Optional.ofNullable(boardColumn.getBoard())
        .map(Board::getId)
        .orElseThrow(() -> new BoardNotFoundException("Board nto found for this column."));

    // check if the new column name is the same as any existing column name for this board
    // if match is found throw exception
    Optional<BoardColumn> existingBoardColumnOpt =
        boardColumnRepository.findBoardColumnByColumnNameAndBoardId(boardColumnDTO.getColumnName(), boardId);

    if (existingBoardColumnOpt.isPresent()) {
      throw new ColumnAlreadyExistsException("A column with that name already exists.");
    }

    // perform update and return dto
    try {
      boardColumn.setColumnName(boardColumnDTO.getColumnName());
      BoardColumn updatedBoardColumn = boardColumnRepository.save(boardColumn);
      return columnMapper.toDTO(updatedBoardColumn);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnUpdateException("There was an error updating this column.", e);
    }
  }

  // delete column
  @Transactional
  public BoardColumnDTO deleteBoardColumn(Long id) {
    // get column by id or else throw exception
    Optional<BoardColumn> optColumn = boardColumnRepository.findById(id);

    if (optColumn.isEmpty()) {
      throw new ColumnNotFoundException("Column not found.");
    }

    // capture the column to be deleted, delete, and return
    try {
      BoardColumn boardColumn = optColumn.get();
      boardColumnRepository.delete(boardColumn);
      return columnMapper.toDTO(boardColumn);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new ColumnDeleteException("There was an error deleting this column.", e);
    }
  }
}
