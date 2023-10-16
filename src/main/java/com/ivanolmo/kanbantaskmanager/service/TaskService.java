package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskDTO;

import java.util.List;

public interface TaskService {
  TaskDTO addTaskToColumn(Long columnId, TaskDTO taskDTO);
  TaskDTO updateTask(Long id, TaskDTO taskDTO);
  TaskDTO deleteTask(Long id);
}
