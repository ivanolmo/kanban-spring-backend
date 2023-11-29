package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.util.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {
  private final ColumnRepository columnRepository;
  private final BoardRepository boardRepository;
  private final ColumnMapper columnMapper;
  private final UserHelper userHelper;

  @Transactional
  public List<ColumnDTO> updateColumns(String boardId, List<ColumnDTO> columnDTOs) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get board by id
    Board board = boardRepository.findByIdAndUserId(boardId, user.getId())
        .orElseThrow(() -> new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // check if board exists and belongs to the current user
    if (!board.getUser().getId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to update columns on this board", HttpStatus.FORBIDDEN);
    }

    // get the existing columns from db and create a map of those columns
    List<Column> existingColumns =
        columnRepository.findAllByBoardId(boardId).orElse(Collections.emptyList());
    Map<String, Column> existingColumnsMap =
        existingColumns.stream().collect(Collectors.toMap(Column::getId, Function.identity()));

    // create or update columns based on the provided DTOs
    List<Column> updatedColumns = columnDTOs.stream().map(dto -> {
      Column column = existingColumnsMap.getOrDefault(dto.getId(), new Column());
      column.setName(dto.getName());
      column.setColor(dto.getColor());
      column.setBoard(board);
      return column;
    }).toList();

    // save updated columns
    List<Column> savedColumns = columnRepository.saveAll(updatedColumns);

    // get the IDs of the columns that should remain
    Set<String> dtoIds = columnDTOs.stream().map(ColumnDTO::getId).collect(Collectors.toSet());

    // determine columns to be deleted
    List<Column> columnsToDelete =
        existingColumns.stream().filter(existingColumn -> !dtoIds.contains(existingColumn.getId()))
            .collect(Collectors.toList());

    // delete columns that are not in the updated DTO list
    columnRepository.deleteAll(columnsToDelete);
    columnRepository.flush();

    return savedColumns.stream().map(columnMapper::toDTO).collect(Collectors.toList());
  }
}
