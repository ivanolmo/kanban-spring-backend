package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskCreationRequest;
import com.ivanolmo.kanbantaskmanager.entity.dto.TaskDTO;
import com.ivanolmo.kanbantaskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {
  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping
  public ResponseEntity<TaskDTO> addTaskToColumn(@Valid @RequestBody TaskCreationRequest request) {
    TaskDTO newTaskDTO = taskService.addTaskToColumn(request.getColumnId(), request.getTask());

    log.info("Successfully added a new task to column with id: {}", request.getColumnId());
    return new ResponseEntity<>(newTaskDTO, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskDTO> updateTask(@Valid @RequestBody TaskDTO taskDTO,
                                            @PathVariable Long id) {
    TaskDTO updatedTaskDTO = taskService.updateTask(id, taskDTO);

    log.info("Successfully updated the task with id: {}", id);
    return new ResponseEntity<>(updatedTaskDTO, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<TaskDTO> deleteTask(@PathVariable Long id) {
    TaskDTO deletedTask = taskService.deleteTask(id);

    log.info("Successfully deleted the task with id: {}", id);
    return new ResponseEntity<>(deletedTask, HttpStatus.NO_CONTENT);
  }
}