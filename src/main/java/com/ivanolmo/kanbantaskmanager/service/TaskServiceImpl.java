package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;

  public TaskServiceImpl(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  // create task
  public TaskDTO addTaskToColumn(TaskDTO taskDTO, Long columnId) {
    return null;
  }

  // get task by id
  public Task getTaskById(Long id) {
    return taskRepository.findById(id).orElse(null);
  }

  // update task
  public TaskDTO updateTask(Long id, TaskDTO taskDetails) {
    Task task = getTaskById(id);

    if (task != null) {
      task.setTitle(taskDetails.getTitle());
      task.setDescription(taskDetails.getDescription());
      return null;
    }
    return null;
  }

  // delete task
  public TaskDTO deleteTask(Long id) {
    Task task = getTaskById(id);

    if (task != null) {
      taskRepository.delete(task);
    }
    return null;
  }
}
