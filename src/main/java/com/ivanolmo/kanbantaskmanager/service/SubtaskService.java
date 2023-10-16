package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;

public interface SubtaskService {
  SubtaskDTO addSubtaskToTask(Long taskId, SubtaskDTO subtaskDTO);

  SubtaskDTO updateSubtask(Long id, SubtaskDTO subtaskDTO);

  void deleteSubtask(Long id);
}
