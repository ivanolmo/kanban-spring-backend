package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;

public interface TaskService {
  TaskDTO addTaskToColumn(String columnId, TaskDTO taskDTO);

  TaskDTO updateTask(String id, TaskDTO taskDTO);

  void deleteTask(String id);
}
