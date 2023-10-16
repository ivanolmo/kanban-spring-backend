package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.exception.task.TaskNotFoundException;
import com.ivanolmo.kanbantaskmanager.mapper.SubtaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.SubtaskRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SubtaskServiceImpl implements SubtaskService {
  private final SubtaskRepository subtaskRepository;
  private final TaskRepository taskRepository;
  private final SubtaskMapper subtaskMapper;

  public SubtaskServiceImpl(SubtaskRepository subtaskRepository,
                            TaskRepository taskRepository,
                            SubtaskMapper subtaskMapper) {
    this.subtaskRepository = subtaskRepository;
    this.taskRepository = taskRepository;
    this.subtaskMapper = subtaskMapper;
  }

  // create subtask
  @Transactional
  public SubtaskDTO addSubtaskToTask(Long taskId, SubtaskDTO subtaskDTO) {
    // get task, throw error if not found
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new TaskNotFoundException("Task not found."));

    // convert the SubtaskDTO to a Subtask entity and set to task
    Subtask subtask = subtaskMapper.toEntity(subtaskDTO);
    subtask.setTask(task);

    // save to repository and return dto, if error throw exception
    try {
      subtask = subtaskRepository.save(subtask);
      return subtaskMapper.toDTO(subtask);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      // TODO custom
      throw new RuntimeException("Failed to create the task.", e);
    }
  }

  // update subtask
  @Transactional
  public SubtaskDTO updateSubtask(Long id, SubtaskDTO subtaskDTO) {
    // get subtask by id
    Subtask subtask = subtaskRepository.findById(id)
        // TODO custom
        .orElseThrow(() -> new RuntimeException("Subtask not found."));

    // get task that this subtask belongs to
    Long taskId = subtask.getTask().getId();

    // check incoming dto for a title
    // this check is in place to prevent issues when a user only wants to update one subtask value
    if (subtaskDTO.getTitle() != null) {
      // check if the new subtask title is the same as any existing subtask title for this task
      subtaskRepository.findByTitleAndTaskIAndId(subtask.getTitle(), taskId)
          .ifPresent(existingSubtask -> {
            // TODO custom
            throw new RuntimeException("A subtask with that title already exists.");
          });

      // update the title
      subtask.setTitle(subtaskDTO.getTitle());
    }

    // check incoming dto for completed Optional<Boolean> value
    // update if present
    if (subtaskDTO.getCompleted() != null) {
      subtask.setCompleted(subtaskDTO.getCompleted());
    }

    // perform update and return dto
    try {
      Subtask updatedSubtask = subtaskRepository.save(subtask);
      return subtaskMapper.toDTO(updatedSubtask);
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      // TODO custom
      throw new RuntimeException("There was an error updating this subtask.", e);
    }
  }

  // delete subtask
  @Transactional
  public void deleteSubtask(Long id) {
    // delete subtask or throw error if subtask not found
    try {
      subtaskRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.error("An error occurred: {}", e.getMessage());
      // TODO custom
      throw new RuntimeException("Subtask not found.");
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      // TODO custom
      throw new RuntimeException("There was an error deleting this subtask.", e);
    }
  }
}
