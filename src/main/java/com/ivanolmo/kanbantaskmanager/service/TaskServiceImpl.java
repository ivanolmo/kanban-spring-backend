package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.TaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;
  private final ColumnRepository columnRepository;
  private final TaskMapper taskMapper;

  public TaskServiceImpl(TaskRepository taskRepository,
                         ColumnRepository columnRepository,
                         TaskMapper taskMapper) {
    this.taskRepository = taskRepository;
    this.columnRepository = columnRepository;
    this.taskMapper = taskMapper;
  }

  // create task
  @Transactional
  public TaskDTO addTaskToColumn(Long columnId, TaskDTO taskDTO) {
    // get column, throw error if not found
    Column column = columnRepository.findById(columnId)
        .orElseThrow(() -> new EntityOperationException("Column", "read", HttpStatus.NOT_FOUND));

    // if new task title already exists for this column, throw error
    taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), columnId)
        .ifPresent(existingTask -> {
          throw new EntityOperationException("A task with that title already exists.",
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
  public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
    // get task by id or else throw exception
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new EntityOperationException("Task", "read", HttpStatus.NOT_FOUND));

    // get column that this task belongs to
    Long columnId = task.getColumn().getId();

    // check incoming dto for a title
    // this check is in place to prevent issues when a user only wants to update one task value
    if (taskDTO.getTitle() != null) {
      // check if the new task title is the same as any existing task title for this column
      // if match is found throw exception
      taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), columnId)
          .ifPresent(existingTask -> {
            throw new EntityOperationException("A task with that title already exists.",
                HttpStatus.CONFLICT);
          });

      // update the title
      task.setTitle(taskDTO.getTitle());
    }

    // check incoming dto for a description
    // update if present
    if (taskDTO.getDescription() != null) {
      task.setDescription(taskDTO.getDescription());
    }

    // perform update and return dto
    try {
      Task updatedTask = taskRepository.save(task);
      return taskMapper.toDTO(updatedTask);
    } catch (Exception e) {
      log.error("An error occurred while updating task '{}': {}",
          taskDTO.getTitle(), e.getMessage());
      throw new EntityOperationException("Task", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // delete task
  @Transactional
  public void deleteTask(Long id) {
    // delete task or throw error if task not found
    try {
      taskRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.error("An error occurred while deleting task id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Task", "delete", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      log.error("An error occurred while deleting task id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Task", "delete", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
