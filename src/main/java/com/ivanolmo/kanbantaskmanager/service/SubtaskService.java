package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;

public interface SubtaskService {
  SubtaskDTO addSubtaskToTask(String taskId, SubtaskDTO subtaskDTO);

  SubtaskDTO updateSubtask(String id, SubtaskDTO subtaskDTO);

  void deleteSubtask(String id);
}
