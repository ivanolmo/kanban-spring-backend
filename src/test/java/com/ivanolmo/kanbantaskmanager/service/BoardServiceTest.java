package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.BoardInfo;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.BoardMapper;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
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
  private BoardMapper boardMapper;
  @MockBean
  private UserHelper userHelper;
  @Autowired
  private BoardService boardService;
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

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findAllByUserId(anyString());
    verify(boardMapper, times(3)).toDTO(any(Board.class));
  }

  @Test
  public void testGetAllUserBoards_UserNotFoundException() {
    // given
    // N/A for this test

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.getAllUserBoards());
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testGetBoardById() {
    // given
    Board board = Board.builder().id("board").user(user).build();
    BoardDTO boardDTO = BoardDTO.builder().id(board.getId()).build();

    // when
    when(boardRepository.findByIdAndUserId(board.getId(), user.getId())).thenReturn(Optional.of(board));
    when(boardMapper.toDTO(any(Board.class))).thenReturn(boardDTO);

    // then
    BoardDTO result = boardService.getBoardById(board.getId());
    assertNotNull(result, "Result should not be null");
    assertEquals(board.getId(), result.getId(), "The board IDs should be the same");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(boardMapper).toDTO(board);
  }

  @Test
  public void testGetBoardById_BoardNotFoundException() {
    // given
    String boardId = "board";

    // when
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
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.empty());
    when(boardMapper.toEntity(boardDTO)).thenReturn(board);
    when(boardRepository.save(any(Board.class))).thenReturn(board);
    when(boardMapper.toDTO(board)).thenReturn(returnedBoardDTO);

    // then
    BoardDTO result = boardService.addBoardToUser(boardDTO);
    assertNotNull(result, "Board DTO should not be null");
    assertEquals("New Board", result.getName(), "Board name should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByNameAndUserId(anyString(), anyString());
    verify(boardMapper).toEntity(boardDTO);
    verify(boardRepository).save(any(Board.class));
    verify(boardMapper).toDTO(board);
  }

  @Test
  public void testAddBoardToUser_UserNotFoundException() {
    // given
    BoardDTO boardDTO = new BoardDTO();

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.addBoardToUser(boardDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository, never()).save(any(Board.class));
  }

  @Test
  public void testAddBoardToUser_BoardAlreadyExistsException() {
    // given
    BoardDTO boardDTO = BoardDTO.builder().name("Existing Board").build();
    Board board = Board.builder().id("board").name(boardDTO.getName()).user(user).build();

    // when
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.of(board));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.addBoardToUser(boardDTO));
    assertEquals("A board with that name already exists", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByNameAndUserId(boardDTO.getName(), user.getId());
    verify(boardRepository, never()).save(any(Board.class));
  }

  @Test
  public void testAddBoardToUser_BoardCreationException() {
    // given
    BoardDTO boardDTO = BoardDTO.builder().name("New Board").build();
    Board board = Board.builder().name(boardDTO.getName()).user(user).build();

    // when
    when(boardRepository.findByNameAndUserId(boardDTO.getName(), user.getId())).thenReturn(Optional.empty());
    when(boardMapper.toEntity(boardDTO)).thenReturn(board);
    doThrow(new RuntimeException("Error")).when(boardRepository).save(any(Board.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.addBoardToUser(boardDTO));
    assertEquals("Board create operation failed", e.getMessage(), "The exception message should match");

    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByNameAndUserId(anyString(), anyString());
    verify(boardMapper).toEntity(boardDTO);
    verify(boardRepository).save(any(Board.class));
  }

  @Test
  public void testUpdateBoardName() {
    // given
    String updatedName = "Updated Board Name";
    BoardDTO boardDTO = BoardDTO.builder().name(updatedName).build();
    Board existingBoard = Board.builder().id("board").name("Existing Board Name").user(user).build();
    Board updatedBoard = Board.builder().id(existingBoard.getId()).name(updatedName).user(user).build();

    // when
    when(boardRepository.findByIdAndUserId(existingBoard.getId(), user.getId())).thenReturn(Optional.of(existingBoard));
    when(boardRepository.findByNameAndUserId(updatedName, user.getId())).thenReturn(Optional.empty());
    when(boardRepository.save(any(Board.class))).thenReturn(updatedBoard);
    when(boardMapper.toDTO(updatedBoard)).thenReturn(boardDTO);

    // then
    BoardDTO result = boardService.updateBoardName(existingBoard.getId(), updatedName);
    assertNotNull(result, "Board DTO should not be null");
    assertEquals(updatedName, result.getName(), "Board name should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(boardRepository).findByNameAndUserId(anyString(), anyString());
    verify(boardRepository).save(any(Board.class));
    verify(boardMapper).toDTO(existingBoard);
  }

  @Test
  public void testUpdateBoardName_UserNotFoundException() {
    // given
    String updatedName = "Updated Board Name";
    Board board = Board.builder().id("board").build();

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(board.getId(), updatedName));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository, never()).save(any(Board.class));
  }

  @Test
  public void testUpdateBoardName_UserForbiddenException() {
    // given
    User otherUser = User.builder().id("otherUser").email("other@example.com").build();
    Board otherBoard = Board.builder().id("board").user(otherUser).build();

    // when
    when(boardRepository.findByIdAndUserId(otherBoard.getId(), user.getId())).thenReturn(Optional.of(otherBoard));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> boardService.updateBoardName(otherBoard.getId(), "Updated Board Name"));
    assertEquals("You do not have permission to update this board", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(otherBoard.getId(), user.getId());
    verify(boardRepository, never()).save(any(Board.class));
  }

  @Test
  public void testUpdateBoardName_BoardNotFoundException() {
    // given
    String updatedName = "Updated Board Name";
    BoardDTO boardDTO = BoardDTO.builder().id("board").build();

    // when
    when(boardRepository.findByIdAndUserId(boardDTO.getId(), user.getId()))
        .thenThrow(new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(boardDTO.getId(), updatedName));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(boardRepository, never()).save(any(Board.class));
  }

  @Test
  public void testUpdateBoardName_BoardAlreadyExistsException() {
    // given
    String duplicateName = "Duplicate Board Name";
    Board existingBoard = Board.builder().id("board").user(user).build();

    // when
    when(boardRepository.findByIdAndUserId(existingBoard.getId(), user.getId())).thenReturn(Optional.of(existingBoard));
    when(boardRepository.findByNameAndUserId(duplicateName, user.getId())).thenReturn(Optional.of(existingBoard));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(existingBoard.getId(), duplicateName));
    assertEquals("A board with that name already exists", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(boardRepository).findByNameAndUserId(anyString(), anyString());
    verify(boardRepository, never()).save(any(Board.class));
  }

  @Test
  public void testUpdateBoardName_BoardUpdateException() {
    // given
    String updatedName = "Updated Board Name";
    Board board = Board.builder().id("board").name("Existing Name").user(user).build();

    // when
    when(boardRepository.findByIdAndUserId(board.getId(), user.getId())).thenReturn(Optional.of(board));
    when(boardRepository.findByNameAndUserId(updatedName, user.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(boardRepository).save(any(Board.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class, () -> boardService.updateBoardName(board.getId(), updatedName));
    assertEquals("Board update operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(boardRepository).findByNameAndUserId(anyString(), anyString());
    verify(boardRepository).save(any(Board.class));
  }

  @Test
  public void testDeleteBoard() {
    // given
    Board board = Board.builder().id("board").name("Board Name").user(user).build();
    BoardInfo boardInfo =
        BoardInfo.builder().boardId("board").userId(user.getId()).board(board).build();

    // when
    when(boardRepository.findBoardInfoById(board.getId())).thenReturn(Optional.of(boardInfo));
    doNothing().when(boardRepository).deleteById(board.getId());

    // then
    boardService.deleteBoard(board.getId());

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(boardRepository).deleteById(anyString());
  }

  @Test
  public void testDeleteBoard_UserNotFoundException() {
    // given
    Board board = Board.builder().id("board").name("Board Name").user(user).build();

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> boardService.deleteBoard(board.getId()));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should " + "match");

    // verify interactions
    verify(userHelper).getCurrentUser();
  }

  @Test
  public void testDeleteBoard_UserForbiddenException() {
    // given
    User otherUser = User.builder().id("otherUser").email("other@example.com").build();
    Board board = Board.builder().id("board").name("Board Name").user(otherUser).build();
    BoardInfo boardInfo =
        BoardInfo.builder().boardId("board").userId(otherUser.getId()).board(board).build();

    // when
    when(boardRepository.findBoardInfoById(board.getId())).thenReturn(Optional.of(boardInfo));

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> boardService.deleteBoard(board.getId()));
    assertEquals("You do not have permission to delete this board", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(boardRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteBoard_BoardNotFoundException() {
    // given
    Board board = Board.builder().id("board").user(user).build();

    // when
    when(boardRepository.findBoardInfoById(board.getId()))
        .thenThrow(new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> boardService.deleteBoard(board.getId()));
    assertEquals("Board read operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(boardRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteBoard_BoardDeleteException() {
    // given
    Board board = Board.builder().id("board").user(user).build();
    BoardInfo boardInfo =
        BoardInfo.builder().boardId("board").userId(user.getId()).board(board).build();

    // when
    when(boardRepository.findBoardInfoById(board.getId())).thenReturn(Optional.of(boardInfo));
    doThrow(new EntityOperationException("Board", "delete", HttpStatus.INTERNAL_SERVER_ERROR)).when(boardRepository).deleteById(board.getId());

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> boardService.deleteBoard(board.getId()));
    assertEquals("Board delete operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findBoardInfoById(anyString());
    verify(boardRepository).deleteById(anyString());
  }
}
