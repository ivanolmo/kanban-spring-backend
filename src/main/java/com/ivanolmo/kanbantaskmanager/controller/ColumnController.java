package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnCreationRequest;
import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.service.ColumnService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/columns")
@Slf4j
public class ColumnController {
  private final ColumnService columnService;

  public ColumnController(ColumnService columnService) {
    this.columnService = columnService;
  }

  @PostMapping
  public ResponseEntity<ColumnDTO> addColumnToBoard(@Valid @RequestBody ColumnCreationRequest request) {
    ColumnDTO newColumnDTO = columnService.addColumnToBoard(request.getBoardId(), request.getColumn());

    log.info("Successfully added a new column to board with id: {}", request.getBoardId());
    return new ResponseEntity<>(newColumnDTO, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ColumnDTO> updateColumnName(@Valid @RequestBody ColumnDTO columnDTO,
                                                    @PathVariable Long id) {
    ColumnDTO updatedColumnDTO = columnService.updateColumnName(id, columnDTO);

    log.info("Successfully updated the column with id: {}", id);
    return new ResponseEntity<>(updatedColumnDTO, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteColumn(@PathVariable Long id) {
    columnService.deleteColumn(id);

    log.info("Successfully deleted the column with id: {}", id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
