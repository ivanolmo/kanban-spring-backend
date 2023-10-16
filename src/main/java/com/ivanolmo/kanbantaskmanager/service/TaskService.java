package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;

public interface TaskService {
  TaskDTO addTaskToColumn(Long columnId, TaskDTO taskDTO);

  TaskDTO updateTask(Long id, TaskDTO taskDTO);

  void deleteTask(Long id);
}
