package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.dto.SubtaskInfo;
import com.ivanolmo.kanbantaskmanager.dto.TaskInfo;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubtaskServiceImpl implements SubtaskService {
  private final SubtaskRepository subtaskRepository;
  private final TaskRepository taskRepository;
  private final SubtaskMapper subtaskMapper;
  private final UserHelper userHelper;

  // create subtask
  @Transactional
  public SubtaskDTO addSubtaskToTask(String taskId, SubtaskDTO subtaskDTO) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get task and user info
    TaskInfo taskInfo = taskRepository.findTaskInfoById(taskId)
        .orElseThrow(() -> new EntityOperationException("Task", "read", HttpStatus.NOT_FOUND));

    // check that subtask -> task relation and user id matches
    if (!taskInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to add a subtask to this task", HttpStatus.FORBIDDEN);
    }

    Task task = taskInfo.getTask();

    // if new subtask title already exists for this task, throw error
    subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), taskId)
        .ifPresent(existingSubtask -> {
          throw new EntityOperationException("A subtask with that title already exists",
              HttpStatus.CONFLICT);
        });

    // convert the SubtaskDTO to a Subtask entity and set to task
    Subtask subtask = subtaskMapper.toEntity(subtaskDTO);
    subtask.setTask(task);

    // save and return dto, throw error if exception
    try {
      subtask = subtaskRepository.save(subtask);
      return subtaskMapper.toDTO(subtask);
    } catch (Exception e) {
      log.error("An error occurred while adding subtask '{}' to task '{}': {}",
          subtaskDTO.getTitle(), task.getTitle(), e.getMessage());
      throw new EntityOperationException("Subtask", "create", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // update subtask
  @Transactional
  public SubtaskDTO updateSubtask(String id, SubtaskDTO subtaskDTO) {
    // Get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get subtask and user info
    SubtaskInfo subtaskInfo = subtaskRepository.findSubtaskInfoById(id)
        .orElseThrow(() -> new EntityOperationException("Subtask", "read", HttpStatus.NOT_FOUND));

    // check if subtask info and user id matches
    if (!subtaskInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to update a subtask in this task", HttpStatus.FORBIDDEN);
    }

    Subtask subtask = subtaskInfo.getSubtask();
    Task task = subtask.getTask();

    // check incoming dto for a title
    // update if present
    if (subtaskDTO.getTitle() != null) {
      // check if the new subtask title is the same as any existing subtask title for this task
      subtaskRepository.findByTitleAndTaskId(subtaskDTO.getTitle(), task.getId())
          .ifPresent(existingSubtask -> {
            throw new EntityOperationException("A subtask with that title already exists",
                HttpStatus.CONFLICT);
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
      log.error("An error occurred while updating subtask '{}': {}",
          subtaskDTO.getTitle(), e.getMessage());
      throw new EntityOperationException("Subtask", "update", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // delete subtask
  @Transactional
  public void deleteSubtask(String id) {
    // get user from security context via helper method
    User user = userHelper.getCurrentUser();

    // get subtask and user info
    SubtaskInfo subtaskInfo = subtaskRepository.findSubtaskInfoById(id)
        .orElseThrow(() -> new EntityOperationException("Subtask", "read", HttpStatus.NOT_FOUND));

    // check if subtask info and user id matches
    if (!subtaskInfo.getUserId().equals(user.getId())) {
      throw new EntityOperationException(
          "You do not have permission to delete a subtask from this task", HttpStatus.FORBIDDEN);
    }

    // delete subtask or throw error if task not found
    try {
      subtaskRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.error("An error occurred while deleting subtask id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Subtask", "delete", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      log.error("An error occurred while deleting subtask id '{}': {}",
          id, e.getMessage());
      throw new EntityOperationException("Subtask", "delete", e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
