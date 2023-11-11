package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.EntityOperationException;
import com.ivanolmo.kanbantaskmanager.mapper.SubtaskMapper;
import com.ivanolmo.kanbantaskmanager.repository.SubtaskRepository;
import com.ivanolmo.kanbantaskmanager.repository.TaskRepository;
import com.ivanolmo.kanbantaskmanager.util.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubtaskServiceImpl implements SubtaskService {
  private final SubtaskRepository subtaskRepository;
  private final TaskRepository taskRepository;
  private final SubtaskMapper subtaskMapper;
  private final UserHelper userHelper;

  // update subtask
  @Transactional
  public List<SubtaskDTO> updateSubtasks(String taskId, List<SubtaskDTO> subtaskDTOs) {
    // Get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get task by id
    Task task = taskRepository.findById(taskId).orElseThrow(
        () -> new EntityOperationException("Task", "read", HttpStatus.NOT_FOUND));

    // check if task belongs to user
    if (!task.getColumn().getBoard().getUser().getId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to update subtasks on this task", HttpStatus.FORBIDDEN);
    }

    // get the existing subtasks from db and create a map of those subtasks
    List<Subtask> existingSubtasks =
        subtaskRepository.findAllByTaskId(taskId).orElse(Collections.emptyList());
    Map<String, Subtask> existingSubtasksMap = existingSubtasks.stream()
        .collect(Collectors.toMap(Subtask::getId, Function.identity()));

    // create or update subtasks based on the provided DTOs
    List<Subtask> updatedSubtasks = subtaskDTOs.stream()
        .map(dto -> {
          Subtask subtask = existingSubtasksMap.getOrDefault(dto.getId(), new Subtask());
          subtask.setTitle(dto.getTitle());
          subtask.setCompleted(dto.getCompleted());
          subtask.setTask(task);
          return subtask;
        }).collect(Collectors.toList());

    // save updated subtasks
    List<Subtask> savedSubtasks = subtaskRepository.saveAll(updatedSubtasks);

    // get the IDs of the subtasks that should remain
    Set<String> dtoIds = subtaskDTOs.stream()
        .map(SubtaskDTO::getId)
        .collect(Collectors.toSet());

    // determine subtasks to be deleted
    List<Subtask> subtasksToDelete = existingSubtasks.stream()
        .filter(existingSubtask -> !dtoIds.contains(existingSubtask.getId()))
        .collect(Collectors.toList());

    // delete subtasks that are not in the updated DTO list
    subtaskRepository.deleteAllInBatch(subtasksToDelete);

    return savedSubtasks.stream().map(subtaskMapper::toDTO).collect(Collectors.toList());
  }

  @Transactional
  public SubtaskDTO toggleSubtaskCompletion(String id) {
    // Get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get subtask by id
    Subtask subtaskToToggle = subtaskRepository.findById(id).orElseThrow(
        () -> new EntityOperationException("Subtask", "read", HttpStatus.NOT_FOUND));

    if (!subtaskToToggle.getTask().getColumn().getBoard().getUser().getId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to update this subtask", HttpStatus.FORBIDDEN);
    }

    subtaskToToggle.setCompleted(!subtaskToToggle.getCompleted());

    try {
      Subtask updatedSubtask = subtaskRepository.save(subtaskToToggle);
      return subtaskMapper.toDTO(updatedSubtask);
    } catch (Exception e) {
      log.error("An error occurred while updating subtask id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Task", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
