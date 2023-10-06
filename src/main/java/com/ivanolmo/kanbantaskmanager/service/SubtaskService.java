package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Subtask;

import java.util.List;

public interface SubtaskService {
  Subtask createSubtask(Subtask subtask);
  List<Subtask> getAllUserSubtasks(Long userId);
  Subtask getSubtaskById(Long id);
  Subtask updateSubtask(Long id, Subtask subtaskDetails);
  void deleteSubtask(Long id);
}
