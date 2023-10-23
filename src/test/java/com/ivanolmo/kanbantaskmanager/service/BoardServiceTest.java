package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.BoardMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;
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

import java.util.Arrays;
import java.util.List;
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
  private String username;
  private User user;

  @BeforeEach
  public void setUp() {
    username = "user@example.com";
    user = User.builder().id("user").email(username).build();

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication authentication = Mockito.mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);

    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn(username);
  }

  @Test
  public void testGetAllUserBoards() {
    // given
    Board board1 = new Board();
    Board board2 = new Board();
    Board board3 = new Board();
    List<Board> userBoards = Arrays.asList(board1, board2, board3);

    BoardDTO boardDTO1 = new BoardDTO();
    BoardDTO boardDTO2 = new BoardDTO();
    BoardDTO boardDTO3 = new BoardDTO();
    List<BoardDTO> returnedBoardDTOs = Arrays.asList(boardDTO1, boardDTO2, boardDTO3);

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.findAllByUserId(user.getId())).thenReturn(Optional.of(userBoards));
    when(boardMapper.toDTO(board1)).thenReturn(boardDTO1);
    when(boardMapper.toDTO(board2)).thenReturn(boardDTO2);
    when(boardMapper.toDTO(board3)).thenReturn(boardDTO3);

    // then
    List<BoardDTO> result = boardService.getAllUserBoards();
    assertNotNull(result, "Result should not be null");
    assertEquals(returnedBoardDTOs.size(), result.size(), "The number of boards should match");
    assertEquals(boardDTO1, result.get(0), "The BoardDTO instances should match");
    assertEquals(boardDTO2, result.get(1), "The BoardDTO instances should match");
    assertEquals(boardDTO3, result.get(2), "The BoardDTO instances should match");
  }

  @Test
  public void testGetAllUserBoards_userNotFoundException() {
    // given
    // N/A for this test

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.getAllUserBoards());
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testGetBoardById() {
    // given
    User user = User.builder().id("user").email(username).build();
    Board board = Board.builder().id("board").user(user).build();
    BoardDTO boardDTO = BoardDTO.builder().id(board.getId()).build();

    // when
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    when(boardRepository.findByIdAndUserId(board.getId(), user.getId())).thenReturn(Optional.of(board));
    when(boardMapper.toDTO(any(Board.class))).thenReturn(boardDTO);

    // then
    BoardDTO result = boardService.getBoardById(board.getId());
    assertNotNull(result, "Result should not be null");
    assertEquals(board.getId(), result.getId(), "The board IDs should be the same");
  }

  @Test
  public void testGetBoardById_boardNotFoundException() {
    // given
    String boardId = "board";

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.findByIdAndUserId(boardId, user.getId())).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.getBoardById(boardId));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddBoardToUser() {
    // given
    BoardDTO boardDTO = BoardDTO.builder().name("New Board").build();
    Board board = Board.builder().name(boardDTO.getName()).user(user).build();
    BoardDTO returnedBoardDTO = BoardDTO.builder().name("New Board").build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardMapper.toEntity(boardDTO)).thenReturn(board);
    when(boardRepository.save(any(Board.class))).thenReturn(board);
    when(boardMapper.toDTO(board)).thenReturn(returnedBoardDTO);

    // then
    BoardDTO result = boardService.addBoardToUser(boardDTO);
    assertNotNull(result, "Board DTO should not be null");
    assertEquals("New Board", result.getName(), "Board name should match");

    // verify interactions
    verify(userRepository).findByEmail(username);
    verify(boardRepository).save(any(Board.class));
  }

  @Test
  public void testAddBoardToUser_userNotFoundException() {
    // given
    BoardDTO boardDTO = new BoardDTO();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.addBoardToUser(boardDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddBoardToUser_boardCreationException() {
    // given
    BoardDTO boardDTO = BoardDTO.builder().name("New Board").build();
    Board board = Board.builder().name(boardDTO.getName()).user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardMapper.toEntity(boardDTO)).thenReturn(board);
    doThrow(new RuntimeException("Error")).when(boardRepository).save(any(Board.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.addBoardToUser(boardDTO));
    assertEquals("Board create operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddBoardToUser_boardAlreadyExistsException() {
    // given
    BoardDTO boardDTO = BoardDTO.builder().name("Existing Board").build();
    Board board = Board.builder().name(boardDTO.getName()).user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.of(board));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.addBoardToUser(boardDTO));
    assertEquals("A board with that name already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName() {
    // given
    String updatedName = "Updated Board Name";
    BoardDTO boardDTO = BoardDTO.builder().name(updatedName).build();
    Board existingBoard = Board.builder().id("board").name("Existing Board Name").user(user).build();
    Board updatedBoard = Board.builder().id(existingBoard.getId()).name(updatedName).user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.findByIdAndUserId(existingBoard.getId(), user.getId())).thenReturn(Optional.of(existingBoard));
    when(boardRepository.findByNameAndUserId(updatedName, user.getId())).thenReturn(Optional.empty());
    when(boardRepository.save(any(Board.class))).thenReturn(updatedBoard);
    when(boardMapper.toDTO(updatedBoard)).thenReturn(boardDTO);

    // then
    BoardDTO result = boardService.updateBoardName(existingBoard.getId(), updatedName);
    assertNotNull(result, "Board DTO should not be null");
    assertEquals(updatedName, result.getName(), "Board name should match");

    // verify interactions
    verify(userRepository).findByEmail(username);
    verify(boardRepository).findByIdAndUserId(existingBoard.getId(), user.getId());
    verify(boardRepository).findByNameAndUserId(updatedName, user.getId());
    verify(boardRepository).save(any(Board.class));
  }

  @Test
  public void testUpdateBoardName_boardNotFoundException() {
    // given
    String updatedName = "Updated Board Name";
    BoardDTO boardDTO = BoardDTO.builder().id("board").build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.findByIdAndUserId(boardDTO.getId(), user.getId())).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(boardDTO.getId(), updatedName));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName_userNotFoundException() {
    // given
    String updatedName = "Updated Board Name";
    Board board = Board.builder().id("board").build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(board.getId(), updatedName));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName_boardAlreadyExistsException() {
    // given
    String duplicateName = "Duplicate Board Name";
    Board existingBoard = Board.builder().id("board").user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.findByIdAndUserId(existingBoard.getId(), user.getId())).thenReturn(Optional.of(existingBoard));
    when(boardRepository.findByNameAndUserId(duplicateName, user.getId())).thenReturn(Optional.of(existingBoard));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(existingBoard.getId(), duplicateName));
    assertEquals("A board with that name already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateBoardName_boardUpdateException() {
    // given
    String updatedName = "Updated Board Name";
    Board board = Board.builder().id("board").name("Existing Name").user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.findByIdAndUserId(board.getId(), user.getId())).thenReturn(Optional.of(board));
    when(boardRepository.findByNameAndUserId(updatedName, user.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(boardRepository).save(any(Board.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(board.getId(), updatedName));
    assertEquals("Board update operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testDeleteBoard() {
    // given
    Board board = Board.builder().id("board").user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.existsByIdAndUserId(board.getId(), user.getId())).thenReturn(true);
    doNothing().when(boardRepository).deleteById(board.getId());

    // then
    boardService.deleteBoard(board.getId());

    // verify interactions
    verify(userRepository).findByEmail(username);
    verify(boardRepository).existsByIdAndUserId(board.getId(), user.getId());
  }

  @Test
  public void testDeleteBoard_userNotFound() {
    // given
    Board board = Board.builder().id("board").user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> boardService.deleteBoard(board.getId()));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should " + "match");
  }

  @Test
  public void testDeleteBoard_boardNotFound() {
    // given
    Board board = Board.builder().id("board").user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.existsByIdAndUserId(board.getId(), user.getId())).thenReturn(false);

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> boardService.deleteBoard(board.getId()));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testDeleteBoard_boardDeleteException() {
    // given
    Board board = Board.builder().id("board").user(user).build();

    // when
    when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));
    when(boardRepository.existsByIdAndUserId(board.getId(), user.getId())).thenReturn(true);
    doThrow(new EntityOperationException("Board", "delete", HttpStatus.INTERNAL_SERVER_ERROR)).when(boardRepository).deleteByIdAndUserId(board.getId(), user.getId());

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> boardService.deleteBoard(board.getId()));
    assertEquals("Board delete operation failed", e.getMessage(), "The exception message should " + "match");
  }
}
