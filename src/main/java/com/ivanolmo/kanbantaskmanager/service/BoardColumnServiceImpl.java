package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardNotFoundException;
import com.ivanolmo.kanbantaskmanager.exception.column.ColumnCreationFailedException;
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

  // get column by id
  @Transactional(readOnly = true)
  public BoardColumn getBoardColumnById(Long id) {
    return boardColumnRepository.findById(id).orElse(null);
  }

  // update column
  @Transactional
  public BoardColumn updateBoardColumn(Long id, BoardColumn boardColumnDetails) {
    BoardColumn column = getBoardColumnById(id);

    if (column != null) {
      column.setColumnName(boardColumnDetails.getColumnName());
      return boardColumnRepository.save(column);
    }
    return null;
  }

  // delete column
  @Transactional
  public void deleteBoardColumn(Long id) {
    BoardColumn column = getBoardColumnById(id);

    if (column != null) {
      boardColumnRepository.delete(column);
    }
  }
}
