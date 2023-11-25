package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.ColumnInfo;
import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;
  private final ColumnRepository columnRepository;
  private final SubtaskService subtaskService;
  private final TaskMapper taskMapper;
  private final UserHelper userHelper;

  // create task
  @Transactional
  public TaskDTO addTaskToColumn(String columnId, TaskDTO taskDTO) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get column and user info
    ColumnInfo columnInfo = columnRepository.findColumnInfoById(columnId)
        .orElseThrow(() -> new EntityOperationException("Column", "read", HttpStatus.NOT_FOUND));

    // check that task -> column relation and user id matches
    if (!columnInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to add a task to this column", HttpStatus.FORBIDDEN);
    }

    Column column = columnInfo.getColumn();

    // check if new task title matches an existing title
    taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), columnId)
        .ifPresent(existingTask -> {
          throw new EntityOperationException("A task with that title already exists",
              HttpStatus.CONFLICT);
        });

    // convert the TaskDTO to a Task entity and set to column
    Task task = taskMapper.toEntity(taskDTO);
    task.setColumn(column);

    // save and return dto, throw error if exception
    try {
      task = taskRepository.save(task);
      return taskMapper.toDTO(task);
    } catch (Exception e) {
      log.error("An error occurred while adding task '{}' to column '{}': {}",
          taskDTO.getTitle(), column.getName(), e.getMessage());
      throw new EntityOperationException("Task", "create", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // update task
  @Transactional
  public TaskDTO updateTask(String id, TaskDTO taskDTO) {
    // Get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get task and user info
    TaskInfo taskInfo = taskRepository.findTaskInfoById(id)
        .orElseThrow(() -> new EntityOperationException("Task", "read", HttpStatus.NOT_FOUND));

    // check if task info and user id matches
    if (!taskInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to update a task in this column", HttpStatus.FORBIDDEN);
    }

    Task task = taskInfo.getTask();
    Column column = task.getColumn();

    // handle column change
    if (!taskDTO.getColumnId().equals(column.getId())) {
      Column newColumn = columnRepository.findById(taskDTO.getColumnId())
          .orElseThrow(() -> new EntityOperationException("Column", "read", HttpStatus.NOT_FOUND));
      task.setColumn(newColumn);
    }

    // update task title after checking for duplicates in the same column
    if (!taskDTO.getTitle().equals(task.getTitle())) {
      taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), task.getColumn().getId())
          .ifPresent(existingTask -> {
            throw new EntityOperationException("A task with that title already exists", HttpStatus.CONFLICT);
          });
      task.setTitle(taskDTO.getTitle());
    }

    // update description
    task.setDescription(taskDTO.getDescription());

    // update subtasks and get the updated subtask DTOs
    List<SubtaskDTO> updatedSubtaskDTOs = subtaskService.updateSubtasks(id, taskDTO.getSubtasks());

    // perform update and return dto
    try {
      // save the task after subtask updates
      Task updatedTask = taskRepository.save(task);

      // create and return the updated TaskDTO
      TaskDTO updatedTaskDTO = taskMapper.toDTO(updatedTask);
      updatedTaskDTO.setSubtasks(updatedSubtaskDTOs);
      return updatedTaskDTO;
    } catch (Exception e) {
      log.error("An error occurred while updating task '{}': {}",
          taskDTO.getTitle(), e.getMessage());
      throw new EntityOperationException("Task", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // delete task
  @Transactional
  public void deleteTask(String id) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get task and user info
    TaskInfo taskInfo = taskRepository.findTaskInfoById(id)
        .orElseThrow(() -> new EntityOperationException("Task", "read", HttpStatus.NOT_FOUND));

    // check if task info and user id matches
    if (!taskInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to delete a task from this column", HttpStatus.FORBIDDEN);
    }

    // delete task or throw error if task not found
    try {
      taskRepository.deleteById(id);
    } catch (Exception e) {
      log.error("An error occurred while deleting task id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Task", "delete", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
