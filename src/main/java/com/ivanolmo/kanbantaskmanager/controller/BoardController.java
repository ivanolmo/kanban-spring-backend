package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.exception.BoardNotFoundException;
import com.ivanolmo.kanbantaskmanager.exception.UserNotFoundException;
import com.ivanolmo.kanbantaskmanager.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
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
  public ResponseEntity<?> getAllUserBoards(@RequestParam Long userId, HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    if (csrfToken != null) {
      System.out.println("CSRF token from request attribute: " + csrfToken.getToken());
    }

    if (userId <= 0) {
      throw new UserNotFoundException("Invalid user id.");
    }

    List<Board> boards = boardService.getAllUserBoards(userId);
    if (boards.isEmpty()) {
      throw new BoardNotFoundException("No boards found for this user.");
    }

    logger.info("Successfully retrieved all boards for user with id: {}", userId);
    return new ResponseEntity<>(boards, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getBoardById(@PathVariable Long id) {
    if (id <= 0) {
      throw new BoardNotFoundException("Invalid board ID.");
    }

    Board board = boardService.getBoardById(id);
    if (board == null) {
      throw new BoardNotFoundException("Board not found.");
    }

    logger.info("Successfully retrieved the board with id: {}", board.getId());
    return new ResponseEntity<>(board, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<?> createBoard(@Valid @RequestBody Board board) {
    Board newBoard = boardService.createBoard(board);

    logger.info("Successfully created a new board with id: {}", newBoard.getId());
    return new ResponseEntity<>(newBoard, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateBoard(@Valid @RequestBody Board board, @PathVariable Long id) {
    Board updatedBoard = boardService.updateBoard(id, board);

    logger.info("Successfully updated the board with id: {}", updatedBoard.getId());
    return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
    if (id < 0) {
      throw new BoardNotFoundException("Invalid board ID.");
    }
    boardService.deleteBoard(id);

    logger.info("Successfully deleted the board with id: {}", id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
