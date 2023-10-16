package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Column;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.exception.column.ColumnNotFoundException;
import com.ivanolmo.kanbantaskmanager.exception.task.*;
import com.ivanolmo.kanbantaskmanager.mapper.TaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.ColumnRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    Optional<Column> columnOptional = columnRepository.findById(columnId);
    if (columnOptional.isEmpty()) {
      throw new ColumnNotFoundException("Column not found.");
    }

    // get column from optional, convert the TaskDTO to a Task entity and set to column
    Column column = columnOptional.get();
    Task task = taskMapper.toEntity(taskDTO);
    task.setColumn(column);

    // save to repository and return dto, if error throw exception
    try {
      task = taskRepository.save(task);
      return taskMapper.toDTO(task);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new TaskCreationException("Failed to create the task.", e);
    }
  }

  // update task
  @Transactional
  public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
    // get task by id
    Optional<Task> taskToUpdateOptional = taskRepository.findById(id);

    // throw exception if task is not found
    if (taskToUpdateOptional.isEmpty()) {
      throw new TaskNotFoundException("Task not found.");
    }

    // get task from opt
    Task task = taskToUpdateOptional.get();

    // get column that this task belongs to
    Long columnId = task.getColumn().getId();

    // check incoming dto for a title
    // this check is in place to prevent issues when a user only wants to update one task value
    if (taskDTO.getTitle() != null) {
      // check if the new task title is the same as any existing task title for this column
      Optional<Task> existingTaskTitleOptional =
          taskRepository.findByTitleAndColumnId(taskDTO.getTitle(), columnId);

      // if match is found throw exception
      if (existingTaskTitleOptional.isPresent()) {
        throw new TaskDataAlreadyExistsException("A task with that title already exists.");
      }

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
      log.error("An error occurred: {}", e.getMessage());
      throw new TaskUpdateException("There was an error updating this task.", e);
    }
  }

  // delete task
  @Transactional
  public void deleteTask(Long id) {
    // get task by id or else throw exception
    Optional<Task> taskOptional = taskRepository.findById(id);
    if (taskOptional.isEmpty()) {
      throw new TaskNotFoundException("Task not found.");
    }

    // capture the task to be deleted and delete
    try {
      taskRepository.delete(taskOptional.get());
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      throw new TaskDeleteException("There was an error deleting this task.", e);
    }
  }
}
