package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;

import java.util.List;

public interface SubtaskService {
  List<SubtaskDTO> updateSubtasks(String taskId, List<SubtaskDTO> subtaskDTO);

  SubtaskDTO toggleSubtaskCompletion(String id);
}
