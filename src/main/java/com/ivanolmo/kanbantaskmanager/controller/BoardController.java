package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.exception.board.BoardNotFoundException;
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

    List<Board> boards = boardService.getAllUserBoards(userId);

    logger.info("Successfully retrieved all boards for user with id: {}", userId);
    return new ResponseEntity<>(boards, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getBoardById(@PathVariable Long id) {
    Board board = boardService.getBoardById(id);

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
  public ResponseEntity<?> updateBoardName(@Valid @RequestBody Board board, @PathVariable Long id) {
    Board updatedBoard = boardService.updateBoardName(id, board);

    logger.info("Successfully updated the board with id: {}", updatedBoard.getId());
    return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
    boardService.deleteBoard(id);

    logger.info("Successfully deleted the board with id: {}", id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
