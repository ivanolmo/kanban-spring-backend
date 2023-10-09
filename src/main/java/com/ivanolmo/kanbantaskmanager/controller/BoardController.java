package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.service.BoardService;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
  private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }


  @GetMapping
  public ResponseEntity<?> getAllUserBoards(@RequestParam Long userId) {
    try {
      if (userId <= 0) {
        return new ResponseEntity<>("Invalid user id.", HttpStatus.BAD_REQUEST);
      }

      List<Board> boards = boardService.getAllUserBoards(userId);
      if (boards.isEmpty()) {
        return new ResponseEntity<>("No boards found for this user.", HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(boards, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("An error occurred: {}", e.getMessage());
      return new ResponseEntity<>("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getBoardById(@PathVariable Long id) {
    try {
      if (id <= 0) {
        return new ResponseEntity<>("Invalid board ID.", HttpStatus.BAD_REQUEST);
      }

      Board board = boardService.getBoardById(id);
      if (board == null) {
        return new ResponseEntity<>("Board not found.", HttpStatus.NOT_FOUND);
      }

      return new ResponseEntity<>(board, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Board not found: {}", e.getMessage());
      return new ResponseEntity<>("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping
  public ResponseEntity<?> createBoard(@Valid @RequestBody Board board) {
    try {
      // Validate with @Valid
      Board newBoard = boardService.createBoard(board);

      return new ResponseEntity<>(newBoard, HttpStatus.CREATED);
    } catch (Exception e) {
      logger.error("An error occurred: {}", e.getMessage());
      return new ResponseEntity<>("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateBoard(@Valid @RequestBody Board board, @PathVariable Long id) {
    try {
      // Validate with @Valid
      Board updatedBoard = boardService.updateBoard(id, board);

      return new ResponseEntity<>(updatedBoard, HttpStatus.OK);

    } catch (Exception e) {
      logger.error("An error occurred: {}", e.getMessage());
      return new ResponseEntity<>("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
    try {
      if (id < 0) {
        return new ResponseEntity<>("Invalid board ID.", HttpStatus.BAD_REQUEST);
      }

      boardService.deleteBoard(id);

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      logger.error("Board not found: {}", e.getMessage());
      return new ResponseEntity<>("An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
