package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.ColumnMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ColumnServiceImplTest {
  @MockBean
  private ColumnRepository columnRepository;
  @MockBean
  private BoardRepository boardRepository;
  @MockBean
  private ColumnMapper columnMapper;
  @Autowired
  private ColumnService columnService;

  @Test
  public void testAddColumnToBoard() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName("New Column");

    Board board = new Board();
    board.setId("board");
    board.setName("Test Board");

    Column column = new Column();
    column.setName(columnDTO.getName());
    column.setBoard(board);

    ColumnDTO returnedColumnDTO = new ColumnDTO();
    returnedColumnDTO.setName("New Column");

    // when
    when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
    when(columnMapper.toEntity(columnDTO)).thenReturn(column);
    when(columnRepository.save(any(Column.class))).thenReturn(column);
    when(columnMapper.toDTO(column)).thenReturn(returnedColumnDTO);

    // then
    ColumnDTO result = columnService.addColumnToBoard(board.getId(), columnDTO);
    assertNotNull(result, "Column DTO should not be null");
    assertEquals("New Column", result.getName(), "Column name should match");

    // verify interactions
    verify(columnRepository).save(any(Column.class));
  }

  @Test
  public void testAddColumnToBoard_boardNotFoundException() {
    // given
    String boardId = "board";
    ColumnDTO columnDTO = new ColumnDTO();

    // when
    when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> columnService.addColumnToBoard(boardId, columnDTO));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddColumnToBoard_columnCreationException() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName(""); // fails validation

    Board board = new Board();
    board.setId("board");
    board.setName("Test Board");

    Column column = new Column();
    column.setName(columnDTO.getName());
    column.setBoard(board);

    // when
    when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
    when(columnMapper.toEntity(columnDTO)).thenReturn(column);
    doThrow(new RuntimeException("Error")).when(columnRepository).save(any(Column.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.addColumnToBoard(board.getId(), columnDTO));
    assertEquals("Column create operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(columnRepository).save(any(Column.class));
  }

  @Test
  public void testAddColumnToBoard_columnAlreadyExistsException() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName("Existing Column");

    Board board = new Board();
    board.setId("board");

    Column column = new Column();
    column.setName(columnDTO.getName());
    column.setBoard(board);

    // when
    when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), board.getId())).thenReturn(Optional.of(column));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.addColumnToBoard(board.getId(), columnDTO));
    assertEquals("A column with that name already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateColumnName() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName("Updated Column Name");

    Column existingColumn = new Column();
    existingColumn.setId("column");
    existingColumn.setName("Existing Column Name");

    Board board = new Board();
    board.setId("board");
    existingColumn.setBoard(board);

    Column updatedColumn = new Column();
    updatedColumn.setId(existingColumn.getId());
    updatedColumn.setName(columnDTO.getName());
    updatedColumn.setBoard(board);

    // when
    when(columnRepository.findById(existingColumn.getId())).thenReturn(Optional.of(existingColumn));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), board.getId())).thenReturn(Optional.empty());
    when(columnRepository.save(any(Column.class))).thenReturn(updatedColumn);
    when(columnMapper.toDTO(updatedColumn)).thenReturn(columnDTO);

    // then
    ColumnDTO result = columnService.updateColumnName(existingColumn.getId(), columnDTO);
    assertNotNull(result, "Column DTO should not be null");
    assertEquals(columnDTO.getName(), result.getName(), "Column name should match");

    // verify interactions
    verify(columnRepository).findById(existingColumn.getId());
    verify(columnRepository).findByNameAndBoardId(columnDTO.getName(), board.getId());
    verify(columnRepository).save(any(Column.class));
  }

  @Test
  public void testUpdateColumnName_columnNotFoundException() {
    // given
    String columnId = "column";
    ColumnDTO columnDTO = new ColumnDTO();

    // when
    when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(columnId, columnDTO));
    assertEquals("Column read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateColumnName_boardNotFoundException() {
    // given
    String columnId = "column";
    ColumnDTO columnDTO = new ColumnDTO();
    Column column = new Column();

    // when
    when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(columnId, columnDTO));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateColumnName_columnAlreadyExistsException() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName("Duplicate Board Name");

    Board board = new Board();
    board.setId("board");

    Column existingColumn = new Column();
    existingColumn.setId("column");
    existingColumn.setBoard(board);

    // when
    when(columnRepository.findById(existingColumn.getId())).thenReturn(Optional.of(existingColumn));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), board.getId())).thenReturn(Optional.of(existingColumn));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(existingColumn.getId(), columnDTO));
    assertEquals("A column with that name already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateColumnName_columnUpdateException() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName("Updated Name");

    Column column = new Column();
    column.setId("column");
    column.setName("Existing Name");

    Board board = new Board();
    board.setId("board");
    column.setBoard(board);

    // when
    when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), board.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(columnRepository).save(any(Column.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(column.getId(), columnDTO));
    assertEquals("Column update operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testDeleteColumn() {
    // given
    String columnId = "column";

    // when
    doNothing().when(columnRepository).deleteById(columnId);

    // then
    columnService.deleteColumn(columnId);

    // verify interactions
    verify(columnRepository).deleteById(columnId);
  }

  @Test
  public void testDeleteColumn_columnNotFound() {
    // given
    String columnId = "column";

    // when
    doThrow(EmptyResultDataAccessException.class).when(columnRepository).deleteById(columnId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.deleteColumn(columnId));
    assertEquals("Column delete operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(columnRepository).deleteById(columnId);
  }

  @Test
  public void testDeleteColumn_columnDeleteException() {
    // given
    String columnId = "column";

    // when
    doThrow(RuntimeException.class).when(columnRepository).deleteById(columnId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.deleteColumn(columnId));
    assertEquals("Column delete operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(columnRepository).deleteById(columnId);
  }
}
