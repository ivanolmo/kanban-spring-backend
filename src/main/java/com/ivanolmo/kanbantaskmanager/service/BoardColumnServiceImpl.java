package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import com.ivanolmo.kanbantaskmanager.repository.BoardColumnRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardColumnServiceImpl implements BoardColumnService {
  private final BoardColumnRepository boardColumnRepository;

  public BoardColumnServiceImpl(BoardColumnRepository boardColumnRepository) {
    this.boardColumnRepository = boardColumnRepository;
  }

  // create board column
  public BoardColumn createBoardColumn(BoardColumn boardColumn) {
    return boardColumnRepository.save(boardColumn);
  }

  // get all board columns for a user
  public List<BoardColumn> getAllUserBoardColumns(Long userId) {
    return boardColumnRepository.findByBoardUserId(userId);
  }

  // get column by id
  public BoardColumn getBoardColumnById(Long id) {
    return boardColumnRepository.findById(id).orElse(null);
  }

  // update column
  public BoardColumn updateBoardColumn(Long id, BoardColumn boardColumnDetails) {
    BoardColumn column = getBoardColumnById(id);

    if (column != null) {
      column.setColumnName(boardColumnDetails.getColumnName());
      return boardColumnRepository.save(column);
    }
    return null;
  }

  // delete column
  public void deleteBoardColumn(Long id) {
    BoardColumn column = getBoardColumnById(id);

    if (column != null) {
      boardColumnRepository.delete(column);
    }
  }
}
