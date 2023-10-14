package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.dto.BoardColumnDTO;
import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnCreationRequest;
import com.ivanolmo.kanbantaskmanager.service.BoardColumnService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/columns")
@Slf4j
public class BoardColumnController {
  private final BoardColumnService boardColumnService;

  public BoardColumnController(BoardColumnService boardColumnService) {
    this.boardColumnService = boardColumnService;
  }

  @PostMapping
  public ResponseEntity<BoardColumnDTO> addColumnToBoard(@Valid @RequestBody ColumnCreationRequest request) {
    BoardColumnDTO newBoardColumnDTO = boardColumnService.addColumnToBoard(request.getColumn(),
        request.getBoardId());

    log.info("Successfully added a new column to board with id: {}", request.getBoardId());
    return new ResponseEntity<>(newBoardColumnDTO, HttpStatus.CREATED);
  }
}
