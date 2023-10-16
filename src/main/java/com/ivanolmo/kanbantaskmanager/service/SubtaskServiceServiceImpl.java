package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.exception.task.TaskNotFoundException;
import com.ivanolmo.kanbantaskmanager.mapper.SubtaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.SubtaskRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class SubtaskServiceServiceImpl implements SubtaskService {
  private final SubtaskRepository subtaskRepository;
  private final TaskRepository taskRepository;
  private final SubtaskMapper subtaskMapper;

  public SubtaskServiceServiceImpl(SubtaskRepository subtaskRepository,
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
    Optional<Task> taskOptional = taskRepository.findById(taskId);
    if (taskOptional.isEmpty()) {
      throw new TaskNotFoundException("Task not found.");
    }

    // get task from optional, convert the SubtaskDTO to a Subtask entity and set to task
    Task task = taskOptional.get();
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
    Optional<Subtask> subtaskToUpdateOptional = subtaskRepository.findById(id);

    // throw exception if subtask is not found
    if (subtaskToUpdateOptional.isEmpty()) {
      throw new RuntimeException("Subtask not found.");
    }

    // get subtask from opt
    Subtask subtask = subtaskToUpdateOptional.get();

    // get task that this subtask belongs to
    Long taskId = subtask.getTask().getId();

    // check incoming dto for a title
    // this check is in place to prevent issues when a user only wants to update one subtask value
    if (subtaskDTO.getTitle() != null) {
      // check if the new subtask title is the same as any existing subtask title for this task
      Optional<Subtask> existingSubtaskTitleOptional =
          subtaskRepository.findByTitleAndTaskIAndId(subtask.getTitle(), taskId);

      // if  match is found throw exception
      if (existingSubtaskTitleOptional.isPresent()) {
        // TODO custom
        throw new RuntimeException("A subtask with that title already exists.");
      }

      // update the title
      subtask.setTitle(subtaskDTO.getTitle());
    }

    // check incoming dto for completed Optional<Boolean> value
    // update if present
    if (subtaskDTO.getCompleted().isPresent()) {
      subtask.setCompleted(subtaskDTO.getCompleted().orElse(null));
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
    // get subtask by id
    Optional<Subtask> subtaskOptional = subtaskRepository.findById(id);

    // throw exception if subtask is not found
    if (subtaskOptional.isEmpty()) {
      // TODO custom
      throw new RuntimeException("Subtask not found.");
    }

    // capture the subtask to be deleted and delete
    try {
      subtaskRepository.delete(subtaskOptional.get());
    } catch (Exception e) {
      log.error("An error occurred: {}", e.getMessage());
      // TODO custom
      throw new RuntimeException("There was an error deleting this subtask.", e);
    }
  }
}
