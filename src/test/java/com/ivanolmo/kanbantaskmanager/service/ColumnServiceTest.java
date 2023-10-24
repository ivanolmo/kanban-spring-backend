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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ColumnServiceTest {
  @MockBean
  private ColumnRepository columnRepository;
  @MockBean
  private BoardRepository boardRepository;
  @MockBean
  private ColumnMapper columnMapper;
  @MockBean
  private UserHelper userHelper;
  @Autowired
  private ColumnService columnService;
  private User user;

  @BeforeEach
  public void setUp() {
    String username = "user@example.com";
    user = User.builder().id("user").email(username).build();

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication authentication = Mockito.mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);

    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.getName()).thenReturn(username);

    when(userHelper.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void testAddColumnToBoard() {
    // given
    Board board = Board.builder().id("board").name("Board").user(user).build();
    BoardInfo boardInfo =
        BoardInfo.builder().boardId(board.getId()).userId(user.getId()).board(board).build();
    ColumnDTO newColumnDTO = ColumnDTO.builder().name("New Column").build();
    Column column = Column.builder().name(newColumnDTO.getName()).board(board).build();
    ColumnDTO returnedColumnDTO = ColumnDTO.builder().name(newColumnDTO.getName()).build();

    // when
    when(boardRepository.findBoardInfoById(board.getId())).thenReturn(Optional.of(boardInfo));
    when(columnRepository.findByNameAndBoardId(newColumnDTO.getName(), board.getId())).thenReturn(Optional.empty());
    when(columnMapper.toEntity(newColumnDTO)).thenReturn(column);
    when(columnRepository.save(any(Column.class))).thenReturn(column);
    when(columnMapper.toDTO(column)).thenReturn(returnedColumnDTO);

    // then
    ColumnDTO result = columnService.addColumnToBoard(board.getId(), newColumnDTO);
    assertNotNull(result, "Column DTO should not be null");
    assertEquals("New Column", result.getName(), "Column name should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(columnRepository).findByNameAndBoardId(anyString(), anyString());
    verify(columnMapper).toEntity(any(ColumnDTO.class));
    verify(columnRepository).save(any(Column.class));
    verify(columnMapper).toDTO(any(Column.class));
  }

  @Test
  public void testAddColumnToBoard_UserNotFoundException() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.addColumnToBoard("board", columnDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testAddColumnToBoard_UserForbiddenException() {
    // given
    User otherUser = User.builder().id("otherUser").email("other@example.com").build();
    Board board = Board.builder().id("board").user(otherUser).build();
    BoardInfo boardInfo =
        BoardInfo.builder().boardId(board.getId()).userId(otherUser.getId()).board(board).build();

    ColumnDTO newColumnDTO = ColumnDTO.builder().name("New Column").build();

    // when
    when(boardRepository.findBoardInfoById(board.getId())).thenReturn(Optional.of(boardInfo));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.addColumnToBoard(board.getId(), newColumnDTO));
    assertEquals("You do not have permission to add a column to this board", e.getMessage(),
        "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testAddColumnToBoard_BoardNotFoundException() {
    // given
    String boardId = "board";
    ColumnDTO columnDTO = ColumnDTO.builder().id("column").build();

    // when
    when(boardRepository.findBoardInfoById(boardId))
        .thenThrow(new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> columnService.addColumnToBoard(boardId, columnDTO));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testAddColumnToBoard_ColumnAlreadyExistsException() {
    // given
    Board board = Board.builder().id("board").name("Board").user(user).build();
    BoardInfo boardInfo =
        BoardInfo.builder().boardId(board.getId()).userId(user.getId()).board(board).build();
    ColumnDTO newColumnDTO = ColumnDTO.builder().name("New Column").build();
    Column column = Column.builder().name(newColumnDTO.getName()).board(board).build();

    // when
    when(boardRepository.findBoardInfoById(board.getId())).thenReturn(Optional.of(boardInfo));
    when(columnRepository.findByNameAndBoardId(newColumnDTO.getName(), board.getId())).thenReturn(Optional.of(column));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.addColumnToBoard(board.getId(), newColumnDTO));
    assertEquals("A column with that name already exists", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testAddColumnToBoard_ColumnCreationException() {
    // given
    Board board = Board.builder().id("board").name("Board").build();
    ColumnDTO columnDTO = ColumnDTO.builder().name("New Column").build();
    BoardInfo boardInfo =
        BoardInfo.builder().boardId(board.getId()).userId(user.getId()).board(board).build();
    Column column = Column.builder().name(columnDTO.getName()).board(board).build();

    // when
    when(boardRepository.findBoardInfoById(board.getId())).thenReturn(Optional.of(boardInfo));
    when(columnMapper.toEntity(columnDTO)).thenReturn(column);
    doThrow(new RuntimeException("Error")).when(columnRepository).save(any(Column.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> columnService.addColumnToBoard(board.getId(), columnDTO));
    assertTrue(e.getMessage().contains("Column create operation failed"), "The exception message " +
        "should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(columnRepository).save(any(Column.class));
    verify(columnMapper).toEntity(any(ColumnDTO.class));
  }

  @Test
  public void testUpdateColumnName() {
    // given
    Board board = Board.builder().id("board").name("Board").build();
    ColumnDTO columnDTO = ColumnDTO.builder().name("Updated Column Name").build();
    Column existingColumn =
        Column.builder().id("column").name("Existing Column Name").board(board).build();
    ColumnInfo columnInfo = ColumnInfo.builder().columnId(existingColumn.getId()).userId(user.getId()).column(existingColumn).build();
    Column updatedColumn =
        Column.builder().id(existingColumn.getId()).name(columnDTO.getName()).board(board).build();

    // when
    when(userHelper.getCurrentUser()).thenReturn(user);
    when(columnRepository.findColumnInfoById(existingColumn.getId())).thenReturn(Optional.of(columnInfo));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), board.getId())).thenReturn(Optional.empty());
    when(columnRepository.save(any(Column.class))).thenReturn(updatedColumn);
    when(columnMapper.toDTO(updatedColumn)).thenReturn(columnDTO);

    // then
    ColumnDTO result = columnService.updateColumnName(existingColumn.getId(), columnDTO);
    assertNotNull(result, "Column DTO should not be null");
    assertEquals(columnDTO.getName(), result.getName(), "Column name should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository).findByNameAndBoardId(anyString(), anyString());
    verify(columnRepository).save(any(Column.class));
  }

  @Test
  public void testUpdateColumnName_UserNotFoundException() {
    // given
    ColumnDTO columnDTO = new ColumnDTO();

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.addColumnToBoard("board", columnDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testUpdateColumnName_UserForbiddenException() {
    // given
    String columnId = "column";
    ColumnDTO columnDTO = new ColumnDTO();
    Column column = new Column();
    ColumnInfo columnInfo = ColumnInfo.builder().columnId(columnId).userId("otherUser").column(column).build();

    // when
    when(columnRepository.findColumnInfoById(columnId)).thenReturn(Optional.of(columnInfo));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(columnId, columnDTO));
    assertEquals("You do not have permission to update a column in this board", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testUpdateColumnName_ColumnNotFoundException() {
    // given
    String columnId = "column";
    ColumnDTO columnDTO = new ColumnDTO();

    // when
    when(columnRepository.findColumnInfoById(columnId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(columnId, columnDTO));
    assertEquals("Column read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testUpdateColumnName_ColumnAlreadyExistsException() {
    // given
    Board board = Board.builder().id("board").name("Board").build();
    ColumnDTO columnDTO = ColumnDTO.builder().name("Duplicate Column Name").build();
    Column column = Column.builder().id("column").name("Existing Column Name").board(board).build();
    ColumnInfo columnInfo = ColumnInfo.builder().columnId(column.getId()).userId(user.getId()).column(column).build();

    // when
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), board.getId())).thenReturn(Optional.of(column));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(column.getId(), columnDTO));
    assertEquals("A column with that name already exists", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository).findByNameAndBoardId(anyString(), anyString());
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testUpdateColumnName_ColumnUpdateException() {
    // given
    Board board = Board.builder().id("board").name("Board").build();
    ColumnDTO columnDTO = ColumnDTO.builder().name("Updated Name").build();
    Column column = Column.builder().id("column").name("Existing Name").board(board).build();
    ColumnInfo columnInfo = ColumnInfo.builder().columnId(column.getId()).userId(user.getId()).column(column).build();

    // when
    when(userHelper.getCurrentUser()).thenReturn(user);
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));
    when(columnRepository.findByNameAndBoardId(columnDTO.getName(), board.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(columnRepository).save(any(Column.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumnName(column.getId(), columnDTO));
    assertEquals("Column update operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository).findByNameAndBoardId(anyString(), anyString());
    verify(columnRepository).save(any(Column.class));
  }

  @Test
  public void testDeleteColumn() {
    // given
    Column column = Column.builder().id("column").build();
    ColumnInfo columnInfo =
        ColumnInfo.builder().columnId(column.getId()).userId(user.getId()).column(column).build();

    // when
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));
    doNothing().when(columnRepository).deleteById(column.getId());

    // then
    columnService.deleteColumn(column.getId());

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository).deleteById(anyString());
  }

  @Test
  public void testDeleteColumn_UserNotFoundException() {
    // given
    Column column = Column.builder().id("column").build();

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.deleteColumn(column.getId()));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testDeleteColumn_UserForbiddenException() {
    // given
    Column column = Column.builder().id("column").build();
    ColumnInfo columnInfo =
        ColumnInfo.builder().columnId(column.getId()).userId("otherUser").column(column).build();

    // when
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.deleteColumn(column.getId()));
    assertEquals("You do not have permission to delete a column from this board", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository, never()).save(any(Column.class));
  }

  @Test
  public void testDeleteColumn_ColumnNotFound() {
    // given
    String columnId = "column";

    // when
    when(userHelper.getCurrentUser()).thenReturn(user);
    when(columnRepository.findColumnInfoById(columnId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.deleteColumn(columnId));
    assertEquals("Column read operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteColumn_ColumnDeleteException() {
    // given
    String columnId = "column";
    Column column = new Column();
    column.setId(columnId);
    ColumnInfo columnInfo = ColumnInfo.builder().columnId(columnId).userId(user.getId()).column(column).build();

    // when
    when(columnRepository.findColumnInfoById(columnId)).thenReturn(Optional.of(columnInfo));
    doThrow(RuntimeException.class).when(columnRepository).deleteById(columnId);

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.deleteColumn(columnId));
    assertEquals("Column delete operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(columnRepository).deleteById(anyString());
  }
}
