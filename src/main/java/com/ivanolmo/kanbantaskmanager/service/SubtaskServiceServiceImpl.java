package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import com.ivanolmo.kanbantaskmanager.repository.SubtaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtaskServiceServiceImpl implements SubtaskService {
  private final SubtaskRepository subtaskRepository;

  public SubtaskServiceServiceImpl(SubtaskRepository subtaskRepository) {
    this.subtaskRepository = subtaskRepository;
  }

  // create subtask
  public Subtask createSubtask(Subtask subtask) {
    return subtaskRepository.save(subtask);
  }

  // get all subtasks for a user
  public List<Subtask> getAllUserSubtasks(Long userId) {
    return subtaskRepository.findByTaskBoardColumnBoardUserId(userId);
  }

  // get subtask by id
  public Subtask getSubtaskById(Long id) {
    return subtaskRepository.findById(id).orElse(null);
  }

  // update subtask
  public Subtask updateSubtask(Long id, Subtask subtaskDetails) {
    Subtask subtask = getSubtaskById(id);

    if (subtask != null) {
      subtask.setTitle(subtaskDetails.getTitle());
      subtask.setCompleted(subtaskDetails.getCompleted());
      return subtaskRepository.save(subtask);
    }
    return null;
  }

  // delete subtask
  public void deleteSubtask(Long id) {
    Subtask subtask = getSubtaskById(id);

    if (subtask != null) {
      subtaskRepository.delete(subtask);
    }
  }
}
