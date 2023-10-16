package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.dto.SubtaskCreationRequest;
import com.ivanolmo.kanbantaskmanager.entity.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.service.SubtaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subtasks")
@Slf4j
public class SubtaskController {
  private final SubtaskService subtaskService;

  public SubtaskController(SubtaskService subtaskService) {
    this.subtaskService = subtaskService;
  }

  @PostMapping
  public ResponseEntity<SubtaskDTO> addSubtaskToTask(@Valid @RequestBody SubtaskCreationRequest request) {
    SubtaskDTO newSubtaskDTO = subtaskService.addSubtaskToTask(request.getTaskId(),
        request.getSubtask());

    log.info("Successfully added a new subtask to task with id: {}", request.getTaskId());
    return new ResponseEntity<>(newSubtaskDTO, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<SubtaskDTO> updateSubtask(@Valid @RequestBody SubtaskDTO subtaskDTO,
                                                  @PathVariable Long id) {
    SubtaskDTO updatedSubtaskDTO = subtaskService.updateSubtask(id, subtaskDTO);

    log.info("Successfully updated the subtask with id: {}", id);
    return new ResponseEntity<>(updatedSubtaskDTO, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSubtask(@PathVariable Long id) {
    subtaskService.deleteSubtask(id);

    log.info("Successfully deleted the subtask with id: {}", id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
