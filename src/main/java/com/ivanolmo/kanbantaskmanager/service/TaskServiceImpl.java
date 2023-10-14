package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Task;
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
  public Task createTask(Task task) {
    return taskRepository.save(task);
  }

  // get all tasks for a column
  public List<Task> getAllColumnTasks(Long columnId) {
    return taskRepository.findAllByColumnId(columnId);
  }

  // update task by id
  public Task getTaskById(Long id) {
    return taskRepository.findById(id).orElse(null);
  }

  // update task
  public Task updateTask(Long id, Task taskDetails) {
    Task task = getTaskById(id);

    if (task != null) {
      task.setTitle(taskDetails.getTitle());
      task.setDescription(taskDetails.getDescription());
      task.setCompleted(taskDetails.getCompleted());
      return taskRepository.save(task);
    }
    return null;
  }

  // delete task
  public void deleteTask(Long id) {
    Task task = getTaskById(id);

    if (task != null) {
      taskRepository.delete(task);
    }
  }
}
