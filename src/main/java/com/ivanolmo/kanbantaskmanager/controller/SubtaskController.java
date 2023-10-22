package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskCreationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
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
  public ResponseEntity<ApiResponse<SubtaskDTO>> addSubtaskToTask(@Valid @RequestBody SubtaskCreationRequestDTO request) {
    SubtaskDTO newSubtaskDTO = subtaskService.addSubtaskToTask(request.getTaskId(),
        request.getSubtask());

    log.info("Successfully added a new subtask to task with id: {}", request.getTaskId());
    return ApiResponseUtil.buildSuccessResponse(
        newSubtaskDTO, "Successfully created the subtask", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<SubtaskDTO>> updateSubtask(@Valid @RequestBody SubtaskDTO subtaskDTO,
                                                  @PathVariable String id) {
    SubtaskDTO updatedSubtaskDTO = subtaskService.updateSubtask(id, subtaskDTO);

    log.info("Successfully updated the subtask with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(
        updatedSubtaskDTO, "Successfully updated the subtask", HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteSubtask(@PathVariable String id) {
    subtaskService.deleteSubtask(id);

    log.info("Successfully deleted the subtask with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(null, null, HttpStatus.NO_CONTENT);
  }
}
