package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.TaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
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
public class TaskServiceTest {
  @MockBean
  private TaskRepository taskRepository;
  @MockBean
  private ColumnRepository columnRepository;
  @MockBean
  private TaskMapper taskMapper;
  @Autowired
  private TaskService taskService;

  @Test
  public void testAddTaskToColumn() {
    // given
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setTitle("New Task");
    taskDTO.setDescription("New Task Description");

    Column column = new Column();
    column.setId("column");
    column.setName("Test Column");

    Task task = new Task();
    task.setTitle(taskDTO.getTitle());
    task.setDescription(taskDTO.getDescription());
    task.setColumn(column);

    TaskDTO returnedTaskDTO = new TaskDTO();
    returnedTaskDTO.setTitle("New Task");
    returnedTaskDTO.setDescription("New Task Description");

    // when
    when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
    when(taskMapper.toEntity(taskDTO)).thenReturn(task);
    when(taskRepository.save(any(Task.class))).thenReturn(task);
    when(taskMapper.toDTO(task)).thenReturn(returnedTaskDTO);

    // then
    TaskDTO result = taskService.addTaskToColumn(column.getId(), taskDTO);
    assertNotNull(result, "Task DTO should not be null");
    assertEquals("New Task", result.getTitle(), "Task title should match");
    assertEquals("New Task Description", result.getDescription(), "Task description should match");

    // verify interactions
    verify(taskRepository).save(any(Task.class));
  }

  @Test
  public void testAddTaskToColumn_columnNotFoundException() {
    // given
    String columnId = "column";
    TaskDTO taskDTO = new TaskDTO();

    // when
    when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

    // then
    Exception e = assertThrows(EntityOperationException.class, () -> taskService.addTaskToColumn(columnId, taskDTO));
    assertEquals("Column read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddTaskToColumn_taskAlreadyExistsException() {
    // given
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setTitle("Existing Task");
    taskDTO.setDescription("Existing Task Description");

    Column column = new Column();
    column.setId("column");

    Task task = new Task();
    task.setTitle(taskDTO.getTitle());
    task.setDescription(taskDTO.getDescription());
    task.setColumn(column);

    // when
    when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), column.getId())).thenReturn(Optional.of(task));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
        () -> taskService.addTaskToColumn(column.getId(), taskDTO));
    assertEquals("A task with that title already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddTaskToColumn_taskCreationException() {
    // given
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setTitle(""); // fails validation
    taskDTO.setDescription("New Task Description");

    Column column = new Column();
    column.setId("column");

    Task task = new Task();
    task.setTitle(taskDTO.getTitle());
    task.setDescription(taskDTO.getDescription());
    task.setColumn(column);

    // when
    when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
    when(taskMapper.toEntity(taskDTO)).thenReturn(task);
    doThrow(new RuntimeException("Error")).when(taskRepository).save(any(Task.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> taskService.addTaskToColumn(column.getId(), taskDTO));
    assertEquals("Task create operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(taskRepository).save(any(Task.class));
  }

  @Test
  public void testUpdateTask() {
    // given
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setTitle("Updated Task Title");
    taskDTO.setDescription("Updated Task Description");

    Task existingTask = new Task();
    existingTask.setId("task");
    existingTask.setTitle("Existing Task Title");
    existingTask.setDescription("Existing Task Description");

    Column column = new Column();
    column.setId("column");
    existingTask.setColumn(column);

    Task updatedTask = new Task();
    updatedTask.setId(existingTask.getId());
    updatedTask.setTitle(taskDTO.getTitle());
    updatedTask.setDescription(taskDTO.getDescription());
    updatedTask.setColumn(column);

    // when
    when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), column.getId())).thenReturn(Optional.empty());
    when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
    when(taskMapper.toDTO(updatedTask)).thenReturn(taskDTO);

    // then
    TaskDTO result = taskService.updateTask(existingTask.getId(), taskDTO);
    assertNotNull(result, "Task DTO should not be null");
    assertEquals(taskDTO.getTitle(), result.getTitle(), "Task title should match");
    assertEquals(taskDTO.getDescription(), result.getDescription(), "Task description should match");

    // verify interactions
    verify(taskRepository).findById(existingTask.getId());
    verify(taskRepository).findByTitleAndColumnId(taskDTO.getTitle(), column.getId());
    verify(taskRepository).save(any(Task.class));
  }

  @Test
  public void testUpdateTask_taskNotFoundException() {
    // given
    String taskId = "task";
    TaskDTO taskDTO = new TaskDTO();

    // when
    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> taskService.updateTask(taskId, taskDTO));
    assertEquals("Task read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateTask_columnNotFoundException() {
    // given
    String taskId = "task";
    TaskDTO taskDTO = new TaskDTO();
    Task task = new Task();
    task.setId(taskId);

    // when
    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> taskService.updateTask(taskId, taskDTO));
    assertEquals("Column read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateTask_titleAlreadyExistsException() {
    // given
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setTitle("Existing Task Title");

    Column column = new Column();
    column.setId("column");

    Task existingTask = new Task();
    existingTask.setId("task");
    existingTask.setColumn(column);

    Task anotherTask = new Task();
    anotherTask.setTitle(taskDTO.getTitle());
    anotherTask.setColumn(column);

    // when
    when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), column.getId())).thenReturn(Optional.of(anotherTask));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> taskService.updateTask(existingTask.getId(), taskDTO));
    assertEquals("A task with that title already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateTask_taskCreationException() {
    // given
    TaskDTO taskDTO = new TaskDTO();
    taskDTO.setTitle("Updated Task Title");
    taskDTO.setDescription("Updated Task Description");

    Task task = new Task();
    task.setId("task");
    task.setTitle("Existing Task Title");
    task.setDescription("Existing Task Description");

    Column column = new Column();
    column.setId("column");
    task.setColumn(column);

    // when
    when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
    when(taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), column.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(taskRepository).save(any(Task.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> taskService.updateTask(task.getId(), taskDTO));
    assertEquals("Task update operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testDeleteTask() {
    // given
    String taskId = "task";

    // when
    doNothing().when(taskRepository).deleteById(taskId);

    // then
    taskService.deleteTask(taskId);

    // verify interactions
    verify(taskRepository).deleteById(taskId);
  }

  @Test
  public void testDeleteTask_taskNotFound() {
    // given
    String taskId = "task";

    // when
    doThrow(EmptyResultDataAccessException.class).when(taskRepository).deleteById(taskId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> taskService.deleteTask(taskId));
    assertEquals("Task delete operation failed", e.getMessage(), "The exception message should " +
        "match");

    // verify interactions
    verify(taskRepository).deleteById(taskId);
  }

  @Test
  public void testDeleteTask_taskDeleteException() {
    // given
    String taskId = "task";

    // when
    doThrow(RuntimeException.class).when(taskRepository).deleteById(taskId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> taskService.deleteTask(taskId));
    assertEquals("Task delete operation failed", e.getMessage(), "The exception message should " +
        "match");

    // verify interactions
    verify(taskRepository).deleteById(taskId);
  }
}
