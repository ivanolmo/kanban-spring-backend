package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import com.ivanolmo.kanbantaskmanager.service.SubtaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subtasks")
@RequiredArgsConstructor
@Slf4j
public class SubtaskController {
  private final SubtaskService subtaskService;

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<SubtaskDTO>> toggleSubtaskCompletion(@PathVariable String id) {
    SubtaskDTO toggledSubtaskDTO = subtaskService.toggleSubtaskCompletion(id);

    log.info("Successfully toggled the subtask with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(
        toggledSubtaskDTO, "Successfully toggled the subtask completion", HttpStatus.OK);
  }
}
