package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.dto.SubtaskInfo;
import com.ivanolmo.kanbantaskmanager.dto.TaskInfo;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.SubtaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import com.ivanolmo.kanbantaskmanager.repository.SubtaskRepository;
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
  public void testAddSubtaskToTask() {
    // given
    Task task = Task.builder().id("task").title("Test Task Title").description("Test Task Description").build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(task.getId()).userId(user.getId()).task(task).build();
    SubtaskDTO newSubtaskDTO = SubtaskDTO.builder().title("New Subtask").completed(false).build();
    Subtask subtask = Subtask.builder().title(newSubtaskDTO.getTitle()).completed(newSubtaskDTO.getCompleted()).task(task).build();
    SubtaskDTO returnedSubtaskDTO = SubtaskDTO.builder().title(newSubtaskDTO.getTitle()).completed(newSubtaskDTO.getCompleted()).build();

    // when
    when(taskRepository.findTaskInfoById(task.getId())).thenReturn(Optional.of(taskInfo));
    when(subtaskRepository.findByTitleAndTaskId(newSubtaskDTO.getTitle(), task.getId())).thenReturn(Optional.empty());
    when(subtaskMapper.toEntity(newSubtaskDTO)).thenReturn(subtask);
    when(subtaskRepository.save(subtask)).thenReturn(subtask);
    when(subtaskMapper.toDTO(subtask)).thenReturn(returnedSubtaskDTO);

    // then
    SubtaskDTO result = subtaskService.addSubtaskToTask(task.getId(), newSubtaskDTO);
    assertNotNull(result, "Subtask DTO should not be null");
    assertEquals("New Subtask", result.getTitle(), "Subtask title should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(anyString());
    verify(subtaskRepository).findByTitleAndTaskId(anyString(), anyString());
    verify(subtaskMapper).toEntity(any());
    verify(subtaskRepository).save(subtask);
    verify(subtaskMapper).toDTO(subtask);
  }

  @Test
  public void testAddSubtaskToTask_UserNotFoundException() {
    // given
    String taskId = "task";
    SubtaskDTO subtaskDTO = new SubtaskDTO();

    // when
    when(userHelper.getCurrentUser())
            .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.addSubtaskToTask(taskId, subtaskDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository, never()).save(any(Subtask.class));
  }

  @Test
  public void testAddSubtaskToTask_UserForbiddenException() {
    // given
    User otherUser = User.builder().id("otherUser").email("other@example.com").build();
    Task task = Task.builder().id("task").title("Task").description("Description").build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(task.getId()).userId(otherUser.getId()).task(task).build();
    SubtaskDTO newSubtaskDTO = SubtaskDTO.builder().title("New Subtask Title").completed(false).build();

    // when
    when(taskRepository.findTaskInfoById(task.getId())).thenReturn(Optional.of(taskInfo));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.addSubtaskToTask(task.getId(), newSubtaskDTO));
    assertEquals("You do not have permission to add a subtask to this task", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(anyString());
    verify(subtaskRepository, never()).save(any(Subtask.class));
  }

  @Test
  public void testAddSubtaskToTask_TaskNotFoundException() {
    // given
    String taskId = "task";
    SubtaskDTO subtaskDTO = new SubtaskDTO();

    // when
    when(taskRepository.findTaskInfoById(taskId))
            .thenThrow(new EntityOperationException("Task", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.addSubtaskToTask(taskId, subtaskDTO));
    assertEquals("Task read operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(taskId);
    verify(subtaskRepository, never()).save(any(Subtask.class));
  }

  @Test
  public void testAddSubtaskToTask_SubtaskAlreadyExistsException() {
    // given
    SubtaskDTO subtaskDTO = SubtaskDTO.builder().title("Existing Subtask").completed(false).build();
    Task task = Task.builder().id("task").build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(task.getId()).userId(user.getId()).build();
    Subtask subtask = Subtask.builder().title(subtaskDTO.getTitle()).task(taskInfo.getTask()).build();

    // when
    when(taskRepository.findTaskInfoById(task.getId())).thenReturn(Optional.of(taskInfo));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.of(subtask));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.addSubtaskToTask(task.getId(), subtaskDTO));
    assertEquals("A subtask with that title already exists", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(taskRepository).findTaskInfoById(anyString());
    verify(subtaskRepository).findByTitleAndTaskId(anyString(), anyString());
    verify(subtaskRepository, never()).save(any(Subtask.class));
  }

  @Test
  public void testAddSubtaskToTask_SubtaskCreationException() {
    // given
    SubtaskDTO subtaskDTO = SubtaskDTO.builder().title("").completed(false).build(); // fails validation
    Task task = Task.builder().id("task").build();
    TaskInfo taskInfo = TaskInfo.builder().taskId(task.getId()).userId(user.getId()).task(task).build();
    Subtask subtask = Subtask.builder().title(subtaskDTO.getTitle()).task(taskInfo.getTask()).build();

    // when
    when(taskRepository.findTaskInfoById(task.getId())).thenReturn(Optional.of(taskInfo));
    when(subtaskMapper.toEntity(subtaskDTO)).thenReturn(subtask);
    doThrow(new RuntimeException("Error")).when(subtaskRepository).save(any(Subtask.class));

    // then
    Exception e = assertThrows(EntityOperationException.class,
            () -> subtaskService.addSubtaskToTask(task.getId(), subtaskDTO));
    assertEquals("Subtask create operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).save(any(Subtask.class));
    verify(subtaskMapper).toEntity(any(SubtaskDTO.class));
  }

  @Test
  public void testUpdateSubtask() {
    // given
    Task task = Task.builder().id("task").title("Task Title").description("Task Description").build();
    SubtaskDTO subtaskDTO = SubtaskDTO.builder().title("Updated Title").completed(true).build();
    Subtask existingSubtask = Subtask.builder().id("subtask").title("Existing Title").completed(false).task(task).build();
    SubtaskInfo subtaskInfo = SubtaskInfo.builder().subtaskId(existingSubtask.getId()).userId(user.getId()).subtask(existingSubtask).build();
    Subtask updatedSubtask = Subtask.builder().id(existingSubtask.getId()).title(subtaskDTO.getTitle()).completed(subtaskDTO.getCompleted()).task(task).build();

    // when
    when(subtaskRepository.findSubtaskInfoById(existingSubtask.getId())).thenReturn(Optional.of(subtaskInfo));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.empty());
    when(subtaskRepository.save(any(Subtask.class))).thenReturn(updatedSubtask);
    when(subtaskMapper.toDTO(updatedSubtask)).thenReturn(subtaskDTO);

    // then
    SubtaskDTO result = subtaskService.updateSubtask(existingSubtask.getId(), subtaskDTO);
    assertNotNull(result, "Subtask DTO should not be null");
    assertEquals(subtaskDTO.getTitle(), result.getTitle(), "Subtask title should match");
    assertEquals(subtaskDTO.getCompleted(), result.getCompleted(), "Subtask completion status should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(anyString());
    verify(subtaskRepository).findByTitleAndTaskId(anyString(), anyString());
    verify(subtaskRepository).save(any(Subtask.class));
  }

  @Test
  public void testUpdateSubtask_UserNotFoundException() {
    // given
    SubtaskDTO subtaskDTO = new SubtaskDTO();

    // when
    when(userHelper.getCurrentUser())
            .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.updateSubtask("subtask", subtaskDTO));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository, never()).findById(anyString());
  }

  @Test
  public void testUpdateSubtask_UserForbiddenException() {
    // given
    String subtaskId = "subtask";
    SubtaskDTO subtaskDTO = new SubtaskDTO();
    Subtask subtask = new Subtask();
    SubtaskInfo subtaskInfo = SubtaskInfo.builder().subtaskId(subtaskId).userId("otherUser").subtask(subtask).build();

    // when
    when(subtaskRepository.findSubtaskInfoById(subtaskId)).thenReturn(Optional.of(subtaskInfo));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.updateSubtask(subtaskId, subtaskDTO));
    assertEquals("You do not have permission to update a subtask in this task", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(subtaskId);
  }

  @Test
  public void testUpdateSubtask_SubtaskNotFoundException() {
    // given
    String subtaskId = "subtask";
    SubtaskDTO subtaskDTO = new SubtaskDTO();

    // when
    when(subtaskRepository.findSubtaskInfoById(subtaskId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.updateSubtask(subtaskId, subtaskDTO));
    assertEquals("Subtask read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(subtaskId);
    verify(subtaskRepository, never()).save(any(Subtask.class));
  }

  @Test
  public void testUpdateSubtask_TitleAlreadyExistsException() {
    // given
    SubtaskDTO subtaskDTO = SubtaskDTO.builder().title("Existing Title").build();
    Task task = Task.builder().id("task").build();
    Subtask existingSubtask = Subtask.builder().id("subtask").task(task).build();
    SubtaskInfo subtaskInfo = SubtaskInfo.builder().subtaskId(existingSubtask.getId()).userId(user.getId()).subtask(existingSubtask).build();
    Subtask anotherSubtask = Subtask.builder().title(subtaskDTO.getTitle()).task(task).build();

    // when
    when(subtaskRepository.findSubtaskInfoById(existingSubtask.getId())).thenReturn(Optional.of(subtaskInfo));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.of(anotherSubtask));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.updateSubtask(existingSubtask.getId(), subtaskDTO));
    assertEquals("A subtask with that title already exists", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(anyString());
    verify(subtaskRepository).findByTitleAndTaskId(anyString(), anyString());
    verify(subtaskRepository, never()).save(any(Subtask.class));
  }

  @Test
  public void testUpdateSubtask_SubtaskUpdateException() {
    // given
    Task task = Task.builder().id("column").build();
    SubtaskDTO subtaskDTO = SubtaskDTO.builder().title("Updated Task Title").completed(false).build();
    Subtask subtask = Subtask.builder().id("subtask").title("Existing Subtask Title").completed(false).task(task).build();
    SubtaskInfo subtaskInfo = SubtaskInfo.builder().subtaskId(subtask.getId()).userId(user.getId()).subtask(subtask).build();

    // when
    when(subtaskRepository.findSubtaskInfoById(subtask.getId())).thenReturn(Optional.of(subtaskInfo));
    when(subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())).thenReturn(Optional.empty());
    doThrow(new RuntimeException("Error")).when(subtaskRepository).save(any(Subtask.class));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.updateSubtask(subtask.getId(), subtaskDTO));
    assertEquals("Subtask update operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(anyString());
    verify(subtaskRepository).findByTitleAndTaskId(anyString(), anyString());
    verify(subtaskRepository).save(any(Subtask.class));
  }

  @Test
  public void testDeleteSubtask() {
    // given
    String subtaskId = "subtask";
    Subtask subtask = Subtask.builder().id(subtaskId).build();
    SubtaskInfo subtaskInfo = SubtaskInfo.builder().subtaskId(subtaskId).userId(user.getId()).subtask(subtask).build();

    // when
    when(subtaskRepository.findSubtaskInfoById(subtaskId)).thenReturn(Optional.of(subtaskInfo));
    doNothing().when(subtaskRepository).deleteById(subtaskId);

    // then
    subtaskService.deleteSubtask(subtaskId);

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(subtaskId);
    verify(subtaskRepository).deleteById(subtaskId);
  }

  @Test
  public void testDeleteSubtask_UserNotFoundException() {
    // given
    String subtaskId = "subtask";

    // when
    when(userHelper.getCurrentUser())
            .thenThrow(new EntityOperationException("User", "read", HttpStatus.NOT_FOUND));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.deleteSubtask(subtaskId));
    assertEquals("User read operation failed", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteSubtask_UserForbiddenException() {
    // given
    String subtaskId = "subtask";
    Subtask subtask = Subtask.builder().id(subtaskId).build();
    SubtaskInfo subtaskInfo = SubtaskInfo.builder().subtaskId(subtaskId).userId("otherUser").subtask(subtask).build();

    // when
    when(subtaskRepository.findSubtaskInfoById(subtaskId)).thenReturn(Optional.of(subtaskInfo));

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.deleteSubtask(subtaskId));
    assertEquals("You do not have permission to delete a subtask from this task", e.getMessage(), "The exception message should match");

    // verify
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(subtaskId);
    verify(subtaskRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteSubtask_SubtaskNotFound() {
    // given
    String subtaskId = "subtask";

    // when
    when(subtaskRepository.findSubtaskInfoById(subtaskId)).thenReturn(Optional.empty());

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.deleteSubtask(subtaskId));
    assertEquals("Subtask read operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(subtaskId);
    verify(subtaskRepository, never()).deleteById(anyString());
  }

  @Test
  public void testDeleteSubtask_SubtaskDeleteException() {
    // given
    String subtaskId = "subtask";
    Subtask subtask = Subtask.builder().id(subtaskId).build();
    SubtaskInfo subtaskInfo = SubtaskInfo.builder().subtaskId(subtaskId).userId(user.getId()).subtask(subtask).build();

    // when
    when(subtaskRepository.findSubtaskInfoById(subtaskId)).thenReturn(Optional.of(subtaskInfo));
    doThrow(RuntimeException.class).when(subtaskRepository).deleteById(subtaskId);

    // then
    EntityOperationException e = assertThrows(EntityOperationException.class,
            () -> subtaskService.deleteSubtask(subtaskId));
    assertEquals("Subtask delete operation failed", e.getMessage(), "The exception message should match");

    // verify interactions
    verify(userHelper).getCurrentUser();
    verify(subtaskRepository).findSubtaskInfoById(subtaskId);
    verify(subtaskRepository).deleteById(subtaskId);
  }

}
