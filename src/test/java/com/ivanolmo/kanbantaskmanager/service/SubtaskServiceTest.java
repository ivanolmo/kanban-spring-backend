package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.SubtaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import com.ivanolmo.kanbantaskmanager.repository.SubtaskRepository;
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
public class SubtaskServiceTest {
  @MockBean
  private SubtaskRepository subtaskRepository;
  @MockBean
  private TaskRepository taskRepository;
  @MockBean
  private SubtaskMapper subtaskMapper;
  @Autowired
  private SubtaskService subtaskService;

  @Test
  public void testAddSubtask() {
    // given
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    subtaskDTO.setTitle("New Subtask");
    // Set completed to false initially
    subtaskDTO.setCompleted(false);

    Task task = new Task();
    task.setId("task");
    task.setTitle("Test Task");

    Subtask subtask = new Subtask();
    subtask.setTitle(subtaskDTO.getTitle());
    subtask.setTask(task);

    SubtaskDTO returnedSubtaskDTO = new SubtaskDTO();
    returnedSubtaskDTO.setTitle("New Subtask");

    // when
    when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
    when(subtaskMapper.toEntity(subtaskDTO)).thenReturn(subtask);
    when(subtaskRepository.save(any(Subtask.class))).thenReturn(subtask);
    when(subtaskMapper.toDTO(subtask)).thenReturn(returnedSubtaskDTO);

    // then
    SubtaskDTO result = subtaskService.addSubtaskToTask(task.getId(), subtaskDTO);
    assertNotNull(result, "Subtask DTO should not be null");
    assertEquals("New Subtask", result.getTitle(), "Subtask title should match");

    // verify interactions
    verify(subtaskRepository).save(any(Subtask.class));
  }

  @Test
  public void testAddSubtask_taskNotFoundException() {
    // given
    String taskId = "task";
    SubtaskDTO subtaskDTO = new SubtaskDTO();

    // when
    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.addSubtaskToTask(taskId, subtaskDTO));
    assertEquals("Task read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddSubtask_subtaskAlreadyExistsException() {
    // given
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    subtaskDTO.setTitle("Existing Subtask");

    Task task = new Task();
    task.setId("task");

    Subtask subtask = new Subtask();
    subtask.setTitle(subtaskDTO.getTitle());
    subtask.setTask(task);

    // when
    when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.of(subtask));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.addSubtaskToTask(task.getId(), subtaskDTO));
    assertEquals("A subtask with that title already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testAddSubtask_subtaskCreationException() {
    // given
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    subtaskDTO.setTitle("New Subtask");

    Task task = new Task();
    task.setId("task");

    Subtask subtask = new Subtask();
    subtask.setTitle(subtaskDTO.getTitle());
    subtask.setTask(task);

    // when
    when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
    when(subtaskMapper.toEntity(subtaskDTO)).thenReturn(subtask);
    doThrow(new RuntimeException("Error")).when(subtaskRepository).save(any(Subtask.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.addSubtaskToTask(task.getId(), subtaskDTO));
    assertEquals("Subtask create operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(subtaskRepository).save(any(Subtask.class));
  }

  @Test
  public void testUpdateSubtask() {
    // given
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    subtaskDTO.setTitle("Updated Title");
    subtaskDTO.setCompleted(true);

    Subtask existingSubtask = new Subtask();
    existingSubtask.setId("subtask");
    existingSubtask.setTitle("Existing Title");
    existingSubtask.setCompleted(false);

    Task task = new Task();
    task.setId("task");
    existingSubtask.setTask(task);

    Subtask updatedSubtask = new Subtask();
    updatedSubtask.setId(existingSubtask.getId());
    updatedSubtask.setTitle(subtaskDTO.getTitle());
    updatedSubtask.setCompleted(subtaskDTO.getCompleted());
    updatedSubtask.setTask(task);

    // when
    when(subtaskRepository.findById(existingSubtask.getId())).thenReturn(Optional.of(existingSubtask));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.empty());
    when(subtaskRepository.save(any(Subtask.class))).thenReturn(updatedSubtask);
    when(subtaskMapper.toDTO(updatedSubtask)).thenReturn(subtaskDTO);

    // then
    SubtaskDTO result = subtaskService.updateSubtask(existingSubtask.getId(), subtaskDTO);
    assertNotNull(result, "Subtask DTO should not be null");
    assertEquals(subtaskDTO.getTitle(), result.getTitle(), "Subtask title should match");
    assertEquals(subtaskDTO.getCompleted(), result.getCompleted(), "Subtask completion status should match");

    // verify interactions
    verify(subtaskRepository).findById(existingSubtask.getId());
    verify(subtaskRepository).findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId());
    verify(subtaskRepository).save(any(Subtask.class));
  }

  @Test
  public void testUpdateSubtask_subtaskNotFoundException() {
    // given
    String subtaskId = "subtask";
    SubtaskDTO subtaskDTO = new SubtaskDTO();

    // when
    when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.empty());

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.updateSubtask(subtaskId, subtaskDTO));
    assertEquals("Subtask read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateSubtask_taskNotFoundException() {
    // given
    String subtaskId = "subtask";
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    Subtask subtask = new Subtask();

    // when
    when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.of(subtask));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.updateSubtask(subtaskId, subtaskDTO));
    assertEquals("Task read operation failed", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateSubtask_titleAlreadyExistsException() {
    // given
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    subtaskDTO.setTitle("Duplicate Title");

    Task task = new Task();
    task.setId("task");

    Subtask existingSubtask = new Subtask();
    existingSubtask.setId("subtask");
    existingSubtask.setTask(task);

    // when
    when(subtaskRepository.findById(existingSubtask.getId())).thenReturn(Optional.of(existingSubtask));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.of(existingSubtask));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.updateSubtask(existingSubtask.getId(), subtaskDTO));
    assertEquals("A subtask with that title already exists", e.getMessage(), "The exception message should match");
  }

  @Test
  public void testUpdateSubtask_subtaskCreationException() {
    // given
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    subtaskDTO.setTitle("Updated Title");

    Subtask subtask = new Subtask();
    subtask.setId("subtask");
    subtask.setTitle("Existing Title");

    Task task = new Task();
    task.setId("task");
    subtask.setTask(task);

    // when
    when(subtaskRepository.findById(subtask.getId())).thenReturn(Optional.of(subtask));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(subtaskRepository).save(any(Subtask.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.updateSubtask(subtask.getId(), subtaskDTO));
    assertEquals("Subtask update operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(subtaskRepository).save(any(Subtask.class));
  }


  @Test
  public void testDeleteSubtask() {
    // given
    String subtaskId = "subtask";

    // when
    doNothing().when(subtaskRepository).deleteById(subtaskId);

    // then
    subtaskService.deleteSubtask(subtaskId);

    // verify interactions
    verify(subtaskRepository).deleteById(subtaskId);
  }

  @Test
  public void testDeleteSubtask_taskNotFound() {
    // given
    String subtaskId = "task";

    // when
    doThrow(EmptyResultDataAccessException.class).when(subtaskRepository).deleteById(subtaskId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.deleteSubtask(subtaskId));
    assertEquals("Subtask delete operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(subtaskRepository).deleteById(subtaskId);
  }

  @Test
  public void testDeleteSubtask_taskDeleteException() {
    // given
    String subtaskId = "task";

    // when
    doThrow(RuntimeException.class).when(subtaskRepository).deleteById(subtaskId);

    // then
    Exception e = assertThrows(EntityOperationException.class,
        () -> subtaskService.deleteSubtask(subtaskId));
    assertEquals("Subtask delete operation failed", e.getMessage(), "The exception message should" +
        " " +
        "match");

    // verify interactions
    verify(subtaskRepository).deleteById(subtaskId);
  }
}
