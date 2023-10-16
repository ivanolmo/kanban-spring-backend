package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.dto.SubtaskDTO;

public interface SubtaskService {
  SubtaskDTO addSubtaskToTask(Long taskId, SubtaskDTO subtaskDTO);

  SubtaskDTO updateSubtask(Long id, SubtaskDTO subtaskDetails);

  SubtaskDTO deleteSubtask(Long id);
}
