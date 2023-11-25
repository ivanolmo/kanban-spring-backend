package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnInfo;
import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.dto.TaskInfo;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.TaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest {
  @MockBean
  private TaskRepository taskRepository;
  @MockBean
  private ColumnRepository columnRepository;
  @MockBean
  private TaskMapper taskMapper;
  @MockBean
  private UserHelper userHelper;
  @Autowired
  private TaskService taskService;
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
  public void testAddTaskToColumn() {
    // given
    String columnId = "column";
    Column column = Column.builder().id(columnId).name("Test Column").build();
    ColumnInfo columnInfo =
        ColumnInfo.builder().columnId(column.getId()).userId(user.getId()).column(column).build();
    TaskDTO newTaskDTO =
        TaskDTO.builder().title("New Task").description("New Task Description").build();
    Task task = Task.builder().title(newTaskDTO.getTitle()).description(newTaskDTO.getDescription())
        .column(column).build();
    TaskDTO returnedTaskDTO = TaskDTO.builder().title(newTaskDTO.getTitle())
        .description(newTaskDTO.getDescription()).build();

    // when
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));
    when(taskRepository.findByTitleAndColumnId(newTaskDTO.getTitle(), column.getId()))
        .thenReturn(Optional.empty());
    when(taskMapper.toEntity(newTaskDTO)).thenReturn(task);
    when(taskRepository.save(any(Task.class))).thenReturn(task);
    when(taskMapper.toDTO(task)).thenReturn(returnedTaskDTO);

    // then
    TaskDTO result = taskService.addTaskToColumn(column.getId(), newTaskDTO);
    assertNotNull(result, "Task DTO should not be null");
    assertEquals("New Task", result.getTitle(), "Task title should match");
    assertEquals("New Task Description", result.getDescription(), "Task description should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(taskRepository).findByTitleAndColumnId(anyString(), anyString());
    verify(taskMapper).toEntity(any(TaskDTO.class));
    verify(taskRepository).save(any(Task.class));
    verify(taskMapper).toDTO(any(Task.class));
  }

  @Test
  public void testAddTaskToColumn_UserNotFoundException() {
    // given
    String columnId = "column";
    TaskDTO taskDTO = new TaskDTO();

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> taskService.addTaskToColumn(columnId, taskDTO));
    assertEquals("User read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testAddTaskToColumn_UserForbiddenException() {
    // given
    String columnId = "column";
    User otherUser = User.builder().id("otherUser").email("other@example.com").build();
    Column column = Column.builder().id(columnId).build();
    ColumnInfo columnInfo = ColumnInfo.builder().columnId(column.getId()).userId(otherUser.getId())
        .column(column).build();
    TaskDTO newTaskDTO =
        TaskDTO.builder().title("New Task Title").description("New Task Description").build();

    // when
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> taskService.addTaskToColumn(column.getId(), newTaskDTO));
    assertEquals("You do not have permission to add a task to this column", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testAddTaskToColumn_ColumnNotFoundException() {
    // given
    String columnId = "column";
    TaskDTO taskDTO = new TaskDTO();

    // when
    when(columnRepository.findColumnInfoById(columnId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> taskService.addTaskToColumn(columnId, taskDTO));
    assertEquals("Column read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testAddTaskToColumn_TaskAlreadyExistsException() {
    // given
    String columnId = "column";
    TaskDTO taskDTO =
        TaskDTO.builder().title("Existing Task").description("Existing Task Description").build();
    Column column = Column.builder().id(columnId).build();
    ColumnInfo columnInfo =
        ColumnInfo.builder().columnId(column.getId()).userId(user.getId()).column(column).build();
    Task task = Task.builder().title(taskDTO.getTitle()).description(taskDTO.getDescription())
        .column(column).build();

    // when
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), column.getId()))
        .thenReturn(Optional.of(task));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> taskService.addTaskToColumn(column.getId(), taskDTO));
    assertEquals("A task with that title already exists", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(taskRepository).findByTitleAndColumnId(anyString(), anyString());
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testAddTaskToColumn_TaskCreationFailedException() {
    // given
    String columnId = "column";
    TaskDTO taskDTO =
        TaskDTO.builder().title("New Task").description("New Task Description").build();
    Column column = Column.builder().id(columnId).build();
    ColumnInfo columnInfo =
        ColumnInfo.builder().columnId(column.getId()).userId(user.getId()).column(column).build();
    Task task = Task.builder().title(taskDTO.getTitle()).description(taskDTO.getDescription())
        .column(column).build();

    // when
    when(columnRepository.findColumnInfoById(column.getId())).thenReturn(Optional.of(columnInfo));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), column.getId()))
        .thenReturn(Optional.empty());
    when(taskMapper.toEntity(taskDTO)).thenReturn(task);
    when(taskRepository.save(any(Task.class))).thenThrow(new RuntimeException("Database error"));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> taskService.addTaskToColumn(column.getId(), taskDTO));
    assertEquals("Task create operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(columnRepository).findColumnInfoById(anyString());
    verify(taskRepository).findByTitleAndColumnId(anyString(), anyString());
    verify(taskMapper).toEntity(any(TaskDTO.class));
    verify(taskRepository).save(any(Task.class));
  }

  @Test
  public void testUpdateTask() {
    // given
    String taskId = "task";
    String columnId = "column";
    Column column = Column.builder().id(columnId).name("Test Column").build();
    TaskDTO taskDTO = TaskDTO.builder().title("Updated Task")
        .description("Updated Task Description").columnId(columnId).build();
    Task existingTask = Task.builder().id(taskId).title("Existing Task")
        .description("Existing Task Description").column(column).build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(existingTask.getId()).userId(user.getId())
        .task(existingTask).build();
    Task updatedTask = Task.builder().id(existingTask.getId()).title(taskDTO.getTitle())
        .description(taskDTO.getDescription()).column(column).build();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.of(taskInfo));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), columnId))
        .thenReturn(Optional.empty());
    when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
    when(taskMapper.toDTO(updatedTask)).thenReturn(taskDTO);

    // then
    TaskDTO result = taskService.updateTask(taskId, taskDTO);
    assertNotNull(result, "Task DTO should not be null");
    assertEquals(taskDTO.getTitle(), result.getTitle(), "Task title should match");
    assertEquals(taskDTO.getDescription(), result.getDescription(),
        "Task description should match");

    // verify interactions
    verify(taskRepository).findTaskInfoById(anyString());
    verify(taskRepository).findByTitleAndColumnId(anyString(), anyString());
    verify(taskRepository).save(any(Task.class));
    verify(taskMapper).toDTO(any(Task.class));
  }

  @Test
  public void testUpdateTask_UserNotFoundException() {
    // given
    String taskId = "task";
    TaskDTO taskDTO = new TaskDTO();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.updateTask(taskId, taskDTO));
    assertEquals("Task read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(taskRepository).findTaskInfoById(anyString());
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testUpdateTask_UserForbiddenException() {
    // given
    String taskId = "task";
    String columnId = "column";
    Column column = Column.builder().id(columnId).name("Test Column").build();
    User otherUser = User.builder().id("otherUser").email("other@example.com").build();
    Task existingTask = Task.builder().id(taskId).title("Existing Task")
        .description("Existing Task Description").column(column).build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(existingTask.getId()).userId(otherUser.getId())
        .task(existingTask).build();
    TaskDTO taskDTO =
        TaskDTO.builder().title("Updated Task").description("Updated Task Description").build();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.of(taskInfo));

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.updateTask(taskId, taskDTO));
    assertEquals("You do not have permission to update a task in this column", e.getMessage(),
        "The exception message should match");

    // verify
    verify(taskRepository).findTaskInfoById(anyString());
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testUpdateTask_TaskNotFoundException() {
    // given
    String taskId = "task";
    TaskDTO taskDTO = new TaskDTO();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.updateTask(taskId, taskDTO));
    assertEquals("Task read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(taskRepository).findTaskInfoById(anyString());
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testUpdateTask_TitleAlreadyExistsException() {
    // given
    String taskId = "task";
    String columnId = "column";
    Column column = Column.builder().id(columnId).name("Test Column").build();
    TaskDTO taskDTO = TaskDTO.builder().title("Updated Task")
        .description("Updated Task Description").columnId(columnId).build();
    Task existingTask = Task.builder().id(taskId).title("Existing Task")
        .description("Existing Task Description").column(column).build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(existingTask.getId()).userId(user.getId())
        .task(existingTask).build();
    Task conflictingTask = Task.builder().id("conflict").title(taskDTO.getTitle())
        .description("Conflicting Task Description").column(column).build();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.of(taskInfo));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), columnId))
        .thenReturn(Optional.of(conflictingTask));

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.updateTask(taskId, taskDTO));
    assertEquals("A task with that title already exists", e.getMessage(),
        "The exception message should match");

    // verify
    verify(taskRepository).findTaskInfoById(anyString());
    verify(taskRepository).findByTitleAndColumnId(anyString(), anyString());
    verify(taskRepository, never()).save(any(Task.class));
  }

  @Test
  public void testUpdateTask_TaskUpdateFailedException() {
    // given
    String taskId = "task";
    String columnId = "column";
    Column column = Column.builder().id(columnId).name("Test Column").build();
    TaskDTO taskDTO = TaskDTO.builder().title("Updated Task")
        .description("Updated Task Description").columnId(columnId).build();
    Task existingTask = Task.builder().id(taskId).title("Existing Task")
        .description("Existing Task Description").column(column).build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(existingTask.getId()).userId(user.getId())
        .task(existingTask).build();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.of(taskInfo));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), columnId))
        .thenReturn(Optional.empty());
    when(taskRepository.save(any(Task.class))).thenThrow(new RuntimeException("Database error"));

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.updateTask(taskId, taskDTO));
    assertEquals("Task update operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(taskRepository).findTaskInfoById(anyString());
    verify(taskRepository).findByTitleAndColumnId(anyString(), anyString());
    verify(taskRepository).save(any(Task.class));
  }

  @Test
  public void testDeleteTask() {
    // given
    String taskId = "task";
    Task task = Task.builder().id(taskId).build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(taskId).userId(user.getId()).task(task).build();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.of(taskInfo));
    doNothing().when(taskRepository).deleteById(taskId);

    // then
    taskService.deleteTask(taskId);

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(taskId);
    verify(taskRepository).deleteById(taskId);
  }

  @Test
  public void testDeleteTask_UserNotFoundException() {
    // given
    String taskId = "task";

    // when
    when(userHelper.getCurrentUser())
        .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.deleteTask(taskId));
    assertEquals("User read operation failed", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(taskRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteTask_UserForbiddenException() {
    // given
    String taskId = "task";
    Task task = Task.builder().id(taskId).build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(taskId).userId("otherUser").task(task).build();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.of(taskInfo));

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.deleteTask(taskId));
    assertEquals("You do not have permission to delete a task from this column", e.getMessage(),
        "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(taskId);
    verify(taskRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteTask_TaskNotFoundException() {
    // given
    String taskId = "task";

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.deleteTask(taskId));
    assertEquals("Task read operation failed", e.getMessage(),
        "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(taskId);
    verify(taskRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteTask_TaskDeleteException() {
    // given
    String taskId = "task";
    Task task = Task.builder().id(taskId).build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(taskId).userId(user.getId()).task(task).build();

    // when
    when(taskRepository.findTaskInfoById(taskId)).thenReturn(Optional.of(taskInfo));
    doThrow(RuntimeException.class).when(taskRepository).deleteById(taskId);

    // then
    EntityOperationException e =
        assertThrows(EntityOperationException.class, () -> taskService.deleteTask(taskId));
    assertEquals("Task delete operation failed", e.getMessage(),
        "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(taskId);
    verify(taskRepository).deleteById(taskId);
  }
}
