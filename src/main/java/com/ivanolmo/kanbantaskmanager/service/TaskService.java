package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Task;

import java.util.List;

public interface TaskService {
  Task createTask(Task task);
  List<Task> getAllUserTasks(Long userId);
  Task getTaskById(Long id);
  Task updateTask(Long id, Task taskDetails);
  void deleteTask(Long id);
}
