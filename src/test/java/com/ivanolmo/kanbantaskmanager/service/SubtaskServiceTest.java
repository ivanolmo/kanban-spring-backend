package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.SubtaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import com.ivanolmo.kanbantaskmanager.repository.SubtaskRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class SubtaskServiceTest {
  @MockBean
  private SubtaskRepository subtaskRepository;
  @MockBean
  private TaskRepository taskRepository;
  @MockBean
  private SubtaskMapper subtaskMapper;
  @MockBean
  private UserHelper userHelper;
  @Autowired
  private SubtaskService subtaskService;

  @BeforeEach
  public void setUp() {
    String username = "user@example.com";
    User user = User.builder().id("user").email(username).build();

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication authentication = Mockito.mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);

    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.getName()).thenReturn(username);

    when(userHelper.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void testUpdateSubtasks() {
    // given
    String taskId = "task";
    SubtaskDTO subtaskDTO =
        SubtaskDTO.builder().id("subtask1").title("title").completed(false).build();
    List<SubtaskDTO> subtaskDTOs = Collections.singletonList(subtaskDTO);
    User user = User.builder().id("user").build();
    Board board = Board.builder().user(user).build();
    Column column = Column.builder().board(board).build();
    Task task = Task.builder().id(taskId).column(column).build();

    // when
    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    when(subtaskRepository.findAllByTaskId(taskId))
        .thenReturn(Optional.of(Collections.emptyList()));
    when(subtaskRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

    // then
    List<SubtaskDTO> result = subtaskService.updateSubtasks(taskId, subtaskDTOs);

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findById(taskId);
    verify(subtaskRepository).findAllByTaskId(taskId);
    verify(subtaskRepository).saveAll(anyList());
    verify(subtaskRepository).deleteAllInBatch(anyList());
    assertTrue(result.isEmpty());
  }

  @Test
  public void testUpdateSubtasks_UserNotFoundException() {
    // given
    String taskId = "task";
    SubtaskDTO subtaskDTO =
        SubtaskDTO.builder().id("subtask1").title("title").completed(false).build();
    List<SubtaskDTO> subtaskDTOs = Collections.singletonList(subtaskDTO);

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> subtaskService.updateSubtasks(taskId, subtaskDTOs));
    assertEquals("User read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(taskRepository, never()).findById(anyString());
  }

  @Test
  public void testUpdateSubtasks_UserForbiddenException() {
    // given
    String taskId = "task";
    SubtaskDTO subtaskDTO =
        SubtaskDTO.builder().id("subtask1").title("title").completed(false).build();
    List<SubtaskDTO> subtaskDTOs = Collections.singletonList(subtaskDTO);
    User otherUser = User.builder().id("otherUser").build();
    Board board = Board.builder().user(otherUser).build();
    Column column = Column.builder().board(board).build();
    Task task = Task.builder().id(taskId).column(column).build();

    // when
    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> subtaskService.updateSubtasks(taskId, subtaskDTOs));
    assertEquals("You do not have permission to update subtasks on this task", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findById(taskId);
  }

  @Test
  public void testUpdateSubtasks_TaskNotFoundException() {
    // given
    String taskId = "task";
    SubtaskDTO subtaskDTO =
        SubtaskDTO.builder().id("subtask1").title("title").completed(false).build();
    List<SubtaskDTO> subtaskDTOs = Collections.singletonList(subtaskDTO);

    // when
    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> subtaskService.updateSubtasks(taskId, subtaskDTOs));
    assertEquals("Task read operation failed", e.getMessage(),
        "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findById(taskId);
  }

  @Test
  public void testToggleSubtaskCompletion() {
    // given
    String id = "subtask";
    User user = User.builder().id("user").build();
    Board board = Board.builder().user(user).build();
    Column column = Column.builder().board(board).build();
    Task task = Task.builder().id("task").column(column).build();
    Subtask subtask = Subtask.builder().id(id).task(task).completed(false).build();
    SubtaskDTO subtaskDTO = SubtaskDTO.builder().id(id).completed(true).build();

    // when
    when(subtaskRepository.findById(id)).thenReturn(Optional.of(subtask));
    when(subtaskRepository.save(subtask)).thenReturn(subtask);
    when(subtaskMapper.toDTO(subtask)).thenReturn(subtaskDTO);

    // then
    SubtaskDTO result = subtaskService.toggleSubtaskCompletion(id);

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findById(id);
    verify(subtaskRepository).save(subtask);
    assertTrue(result.getCompleted());
  }

  @Test
  public void testToggleSubtaskCompletion_UserNotFoundException() {
    // given
    String id = "subtask";

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> subtaskService.toggleSubtaskCompletion(id));
    assertEquals("User read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository, never()).findById(anyString());
  }

  @Test
  public void testToggleSubtaskCompletion_UserForbiddenException() {
    // given
    String id = "subtask";
    User otherUser = User.builder().id("otherUser").build();
    Board board = Board.builder().user(otherUser).build();
    Column column = Column.builder().board(board).build();
    Task task = Task.builder().id("task").column(column).build();
    Subtask subtask = Subtask.builder().id(id).task(task).completed(false).build();

    // when
    when(subtaskRepository.findById(id)).thenReturn(Optional.of(subtask));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> subtaskService.toggleSubtaskCompletion(id));
    assertEquals("You do not have permission to update this subtask", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findById(id);
  }

  @Test
  public void testToggleSubtaskCompletion_ToggleSubtaskException() {
    // given
    String id = "subtask";
    User user = User.builder().id("user").build();
    Board board = Board.builder().user(user).build();
    Column column = Column.builder().board(board).build();
    Task task = Task.builder().id("task").column(column).build();
    Subtask subtask = Subtask.builder().id(id).task(task).completed(false).build();

    // when
    when(subtaskRepository.findById(id)).thenReturn(Optional.of(subtask));
    doThrow(RuntimeException.class).when(subtaskRepository).save(subtask);

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> subtaskService.toggleSubtaskCompletion(id));
    assertEquals("Task update operation failed", e.getMessage(),
        "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findById(id);
    verify(subtaskRepository).save(subtask);
  }
}
