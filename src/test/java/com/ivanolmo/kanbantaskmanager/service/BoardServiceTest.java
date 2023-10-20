package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.BoardMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
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
public class BoardServiceTest {
  @MockBean
  private BoardRepository boardRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private BoardMapper boardMapper;
  @Autowired
  private BoardService boardService;

  public BoardServiceTest() {
  }

  @Test
  public void testAddBoardToUser() {
    // given
    BoardDTO boardDTO = new BoardDTO();
    boardDTO.setName("New Board");

    User user = new User();
    user.setId("user");

    Board board = new Board();
    board.setName(boardDTO.getName());
    board.setUser(user);

    BoardDTO returnedBoardDTO = new BoardDTO();
    returnedBoardDTO.setName("New Board");

    // when
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(boardMapper.toEntity(boardDTO)).thenReturn(board);
    when(boardRepository.save(any(Board.class))).thenReturn(board);
    when(boardMapper.toDTO(board)).thenReturn(returnedBoardDTO);

    // then
    BoardDTO result = boardService.addBoardToUser(user.getId(), boardDTO);
    assertNotNull(result, "Board DTO should not be null");
    assertEquals("New Board", result.getName(), "Board name should match");

    // verify interactions
    verify(userRepository).findById(user.getId());
    verify(boardRepository).save(any(Board.class));
  }

  @Test
  public void testAddBoardToUser_userNotFoundException() {
    // given
    String userId = "user";
    BoardDTO boardDTO = new BoardDTO();

    // when
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.addBoardToUser(userId, boardDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddBoardToUser_boardCreationException() {
    // given
    BoardDTO boardDTO = new BoardDTO();
    boardDTO.setName("New Board");

    User user = new User();
    user.setId("user");

    Board board = new Board();
    board.setName(boardDTO.getName());
    board.setUser(user);

    // when
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(boardMapper.toEntity(boardDTO)).thenReturn(board);
    doThrow(new RuntimeException("Error")).when(boardRepository).save(any(Board.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.addBoardToUser(user.getId(), boardDTO));
    assertEquals("Board create operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddBoardToUser_boardAlreadyExistsException() {
    // given
    BoardDTO boardDTO = new BoardDTO();
    boardDTO.setName("Existing Board");

    User user = new User();
    user.setId("user");

    Board board = new Board();
    board.setName(boardDTO.getName());
    board.setUser(user);

    // when
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.of(board));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.addBoardToUser(user.getId(), boardDTO));
    assertEquals("A board with that name already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName() {
    // given
    BoardDTO boardDTO = new BoardDTO();
    boardDTO.setName("Updated Board Name");

    Board existingBoard = new Board();
    existingBoard.setId("board");
    existingBoard.setName("Existing Board Name");

    User user = new User();
    user.setId("user");
    existingBoard.setUser(user);

    Board updatedBoard = new Board();
    updatedBoard.setId(existingBoard.getId());
    updatedBoard.setName(boardDTO.getName());
    updatedBoard.setUser(user);

    // when
    when(boardRepository.findById(existingBoard.getId())).thenReturn(Optional.of(existingBoard));
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.empty());
    when(boardRepository.save(any(Board.class))).thenReturn(updatedBoard);
    when(boardMapper.toDTO(updatedBoard)).thenReturn(boardDTO);

    // then
    BoardDTO result = boardService.updateBoardName(existingBoard.getId(), boardDTO);
    assertNotNull(result, "Board DTO should not be null");
    assertEquals(boardDTO.getName(), result.getName(), "Board name should match");

    // verify interactions
    verify(boardRepository).findById(existingBoard.getId());
    verify(boardRepository).findByNameAndUserId(boardDTO.getName(), user.getId());
    verify(boardRepository).save(any(Board.class));
  }

  @Test
  public void testUpdateBoardName_boardNotFoundException() {
    // given
    String boardId = "board";
    BoardDTO boardDTO = new BoardDTO();

    // when
    when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.updateBoardName(boardId, boardDTO));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName_userNotFoundException() {
    // given
    BoardDTO boardDTO = new BoardDTO();
    Board board = new Board();
    board.setId("board");

    // when
    when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.updateBoardName(board.getId(), boardDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName_boardAlreadyExistsException() {
    // given
    BoardDTO boardDTO = new BoardDTO();
    boardDTO.setName("Duplicate Board Name");

    User user = new User();
    user.setId("user");

    Board existingBoard = new Board();
    existingBoard.setId("board");
    existingBoard.setUser(user);

    // when
    when(boardRepository.findById(existingBoard.getId())).thenReturn(Optional.of(existingBoard));
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.of(existingBoard));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.updateBoardName(existingBoard.getId(), boardDTO));
    assertEquals("A board with that name already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName_boardUpdateException() {
    // given
    BoardDTO boardDTO = new BoardDTO();
    boardDTO.setName("Updated Board Name");

    Board board = new Board();
    board.setId("board");
    board.setName("Existing Name");

    User user = new User();
    user.setId("user");
    board.setUser(user);

    // when
    when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(boardRepository).save(any(Board.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.updateBoardName(board.getId(), boardDTO));
    assertEquals("Board update operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testDeleteBoard() {
    // given
    String boardId = "board";

    // when
    doNothing().when(boardRepository).deleteById(boardId);

    // then
    boardService.deleteBoard(boardId);

    // verify interactions
    verify(boardRepository).deleteById(boardId);
  }

  @Test
  public void testDeleteBoard_boardNotFound() {
    // given
    String boardId = "board";

    // when
    doThrow(EmptyResultDataAccessException.class).when(boardRepository).deleteById(boardId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> boardService.deleteBoard(boardId));
    assertEquals("Board delete operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testDeleteBoard_boardDeleteException() {
    // given
    String boardId = "board";

    // when
    doThrow(RuntimeException.class).when(boardRepository).deleteById(boardId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> boardService.deleteBoard(boardId));
    assertEquals("Board delete operation failed", e.getMessage(), "The exception message should " +
        "match");
  }
}
