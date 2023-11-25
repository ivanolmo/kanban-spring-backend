package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.repository.BoardRepository;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.util.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ColumnServiceTest {
  @MockBean
  private ColumnRepository columnRepository;
  @MockBean
  private BoardRepository boardRepository;
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
  public void testUpdateColumns() {
    // given
    String boardId = "board";
    Board board = Board.builder().id(boardId).user(user).build();
    List<ColumnDTO> columnDTOs =
        List.of(ColumnDTO.builder().id("column1").name("Column 1").color("red").build(),
            ColumnDTO.builder().id("column2").name("Column 2").color("blue").build());

    // when
    when(userHelper.getCurrentUser()).thenReturn(user);
    when(boardRepository.findByIdAndUserId(boardId, user.getId())).thenReturn(Optional.of(board));
    when(columnRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    // then
    List<ColumnDTO> result = columnService.updateColumns(boardId, columnDTOs);
    assertNotNull(result, "Result should not be null");
    assertEquals(columnDTOs.size(), result.size(), "Result size should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(columnRepository).saveAll(anyList());
  }

  @Test
  public void testUpdateColumns_UserNotFoundException() {
    // given
    String boardId = "board";
    ColumnDTO columnDTO = ColumnDTO.builder().id("subtask1").name("name").build();
    List<ColumnDTO> columnDTOs = List.of(columnDTO);

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumns(boardId, columnDTOs));
    assertEquals("User read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository, never()).findById(anyString());
  }

  @Test
  public void testUpdateColumns_UserForbiddenException() {
    // given
    String boardId = "board";
    User otherUser = User.builder().id("otherUser").email("other@example.com").build();
    Board otherBoard = Board.builder().id(boardId).user(otherUser).build();
    List<ColumnDTO> columnDTOs = List.of(ColumnDTO.builder().id("column").build());

    // when
    when(userHelper.getCurrentUser()).thenReturn(user);
    when(boardRepository.findByIdAndUserId(boardId, user.getId()))
        .thenReturn(Optional.of(otherBoard));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumns(boardId, columnDTOs));
    assertEquals("You do not have permission to update columns on this board", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(columnRepository, never()).saveAll(anyList());
  }

  @Test
  public void testUpdateColumns_BoardNotFoundException() {
    // given
    String boardId = "board";
    List<ColumnDTO> columnDTOs = List.of(ColumnDTO.builder().id("column").build());

    // when
    when(userHelper.getCurrentUser()).thenReturn(user);
    when(boardRepository.findByIdAndUserId(boardId, user.getId()))
        .thenThrow(new EntityOperationException("Board", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> columnService.updateColumns(boardId, columnDTOs));
    assertEquals("Board read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(boardRepository).findByIdAndUserId(anyString(), anyString());
    verify(columnRepository, never()).saveAll(anyList());
  }
}
