package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.dto.TaskRequestDTO;
import com.ivanolmo.kanbantaskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
  private final TaskService taskService;

  @PostMapping
  public ResponseEntity<ApiResponse<TaskDTO>> addTaskToColumn(@Valid @RequestBody TaskRequestDTO request) {
    TaskDTO newTaskDTO = taskService.addTaskToColumn(request.getColumnId(), request.getTask());

    log.info("Successfully added a new task to column with id: {}", request.getColumnId());
    return ApiResponseUtil.buildSuccessResponse(newTaskDTO, "Successfully created the task", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<TaskDTO>> updateTask(@Valid @RequestBody TaskRequestDTO request,
                                                         @PathVariable String id) {
    TaskDTO updatedTaskDTO = taskService.updateTask(id, request.getTask());

    log.info("Successfully updated the task with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(updatedTaskDTO, "Successfully updated the task", HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String id) {
    taskService.deleteTask(id);

    log.info("Successfully deleted the task with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(null, null, HttpStatus.NO_CONTENT);
  }
}
