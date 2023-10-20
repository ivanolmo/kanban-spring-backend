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
    String boardId = "board";
    board.setId(boardId);
    board.setName("Test Board");

    Column column = new Column();
    column.setName(columnDTO.getName());
    column.setBoard(board);

    ColumnDTO returnedColumnDTO = new ColumnDTO();
    returnedColumnDTO.setName("New Column");

    // when
    when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
    when(columnMapper.toEntity(columnDTO)).thenReturn(column);
    when(columnRepository.save(any(Column.class))).thenReturn(column);
    when(columnMapper.toDTO(column)).thenReturn(returnedColumnDTO);

    // then
    ColumnDTO result = columnService.addColumnToBoard(boardId, columnDTO);
    assertNotNull(result);
    assertEquals("New Column", result.getName());

    // check interactions
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
    Exception exception = assertThrows(EntityOperationException.class, () ->
        columnService.addColumnToBoard(boardId, columnDTO));
    assertEquals("Board read operation failed", exception.getMessage());
  }

  @Test
  public void testAddColumnToBoard_columnCreationException() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName("");

    Board board = new Board();
    String boardId = "board";
    board.setId(boardId);
    board.setName("Test Board");

    Column column = new Column();
    column.setName(columnDTO.getName());
    column.setBoard(board);

    // when
    when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
    when(columnMapper.toEntity(columnDTO)).thenReturn(column);
    doThrow(new RuntimeException("Error")).when(columnRepository).save(any(Column.class));

    // then
    Exception exception = assertThrows(EntityOperationException.class, () ->
        columnService.addColumnToBoard(boardId, columnDTO));
    assertEquals("Column create operation failed", exception.getMessage());

    // check interactions
    verify(columnRepository).save(any(Column.class));
  }

  @Test
  public void testUpdateColumnName() {
    // given
    String columnId = "column";
    String newName = "Updated Column Name";
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName(newName);

    Board board = new Board();
    String boardId = "board";
    board.setId(boardId);
    board.setName("Test Board");

    Column column = new Column();
    column.setId(columnId);
    column.setName("Old Column Name");
    column.setBoard(board);

    ColumnDTO updatedColumnDTO = new ColumnDTO();
    updatedColumnDTO.setName(newName);

    // when
    when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
    when(columnRepository.findByNameAndBoardId(newName, boardId)).thenReturn(Optional.empty());
    when(columnRepository.save(any(Column.class))).thenReturn(column);
    when(columnMapper.toDTO(any(Column.class))).thenReturn(updatedColumnDTO);

    // then
    ColumnDTO result = columnService.updateColumnName(columnId, columnDTO);
    assertNotNull(result);
    assertEquals(newName, result.getName());

    // check interactions
    verify(columnRepository).findById(columnId);
    verify(columnRepository).findByNameAndBoardId(newName, boardId);
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
    Exception exception = assertThrows(EntityOperationException.class, () ->
        columnService.updateColumnName(columnId, columnDTO));
    assertEquals("Column read operation failed", exception.getMessage());
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
    Exception exception = assertThrows(EntityOperationException.class, () ->
        columnService.updateColumnName(columnId, columnDTO));
    assertEquals("Board read operation failed", exception.getMessage());
  }

  @Test
  public void testUpdateColumnName_columnAlreadyExistsException() {
    // given
    String columnId = "column";
    String newName = "Updated Column Name";
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName(newName);

    Board board = new Board();
    String boardId = "board";
    board.setId(boardId);

    Column column = new Column();
    column.setId(columnId);
    column.setBoard(board);

    // when
    when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
    when(columnRepository.findByNameAndBoardId(newName, boardId)).thenReturn(Optional.of(new Column()));

    // then
    Exception exception = assertThrows(EntityOperationException.class, () ->
        columnService.updateColumnName(columnId, columnDTO));
    assertEquals("A column with that name already exists.", exception.getMessage());
  }

  @Test
  public void testUpdateColumnName_columnUpdateException() {
    // given
    String columnId = "column";
    ColumnDTO columnDTO = new ColumnDTO();
    columnDTO.setName("Updated Name");

    Column column = new Column();
    column.setId(columnId);
    column.setName("Old Name");
    Board board = new Board();
    String boardId = "board";
    board.setId(boardId);
    column.setBoard(board);

    // when
    when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), boardId)).thenReturn(Optional.empty());
    when(columnRepository.save(any(Column.class))).thenThrow(new RuntimeException("Error"));

    // then
    Exception exception = assertThrows(EntityOperationException.class, () ->
        columnService.updateColumnName(columnId, columnDTO));
    assertEquals("Column update operation failed", exception.getMessage());
  }

  @Test
  public void testDeleteColumn() {
    // given
    String columnId = "column";

    // when
    doNothing().when(columnRepository).deleteById(columnId);

    // then
    columnService.deleteColumn(columnId);

    // check interactions
    verify(columnRepository).deleteById(columnId);
  }

  @Test
  public void testDeleteColumn_columnNotFound() {
    // given
    String columnId = "column";

    // when
    doThrow(EmptyResultDataAccessException.class).when(columnRepository).deleteById(columnId);

    // then
    Exception exception = assertThrows(
        EntityOperationException.class,
        () -> columnService.deleteColumn(columnId)
    );
    assertEquals("Column delete operation failed", exception.getMessage());

    // check interactions
    verify(columnRepository).deleteById(columnId);
  }

  @Test
  public void testDeleteColumn_unexpectedError() {
    // given
    String columnId = "column";

    // when
    doThrow(RuntimeException.class).when(columnRepository).deleteById(columnId);

    // then
    Exception exception = assertThrows(
        EntityOperationException.class,
        () -> columnService.deleteColumn(columnId)
    );
    assertEquals("Column delete operation failed", exception.getMessage());

    // check interactions
    verify(columnRepository).deleteById(columnId);
  }
}
