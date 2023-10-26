package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.ColumnCreationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.service.ColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/columns")
@RequiredArgsConstructor
@Slf4j
public class ColumnController {
  private final ColumnService columnService;

  @PostMapping
  public ResponseEntity<ApiResponse<ColumnDTO>> addColumnToBoard(@Valid @RequestBody ColumnCreationRequestDTO request) {
    ColumnDTO newColumnDTO = columnService.addColumnToBoard(request.getBoardId(), request.getColumn());

    log.info("Successfully added a new column to board with id: {}", request.getBoardId());
    return ApiResponseUtil.buildSuccessResponse(
        newColumnDTO, "Successfully created the column", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ColumnDTO>> updateColumnName(@Valid @RequestBody ColumnDTO columnDTO,
                                                    @PathVariable String id) {
    ColumnDTO updatedColumnDTO = columnService.updateColumnName(id, columnDTO);

    log.info("Successfully updated the column with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(
        updatedColumnDTO, "Successfully updated the column", HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteColumn(@PathVariable String id) {
    columnService.deleteColumn(id);

    log.info("Successfully deleted the column with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(null, null, HttpStatus.NO_CONTENT);
  }
}
