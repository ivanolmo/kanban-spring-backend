package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskDTO;

import java.util.List;

public interface TaskService {
  TaskDTO addTaskToColumn(TaskDTO taskDTO, Long columnId);
  Task getTaskById(Long id);
  TaskDTO updateTask(Long id, TaskDTO taskDetails);
  TaskDTO deleteTask(Long id);
}
