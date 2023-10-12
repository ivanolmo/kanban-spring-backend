package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.entity.dto.BoardCreationRequest;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@Slf4j
public class BoardController {
  private final BoardService boardService;

  public BoardController(BoardService boardService) {
    this.boardService = boardService;
  }

  @GetMapping
  public ResponseEntity<List<BoardDTO>> getAllUserBoards(@RequestParam Long userId,
                                             HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    if (csrfToken != null) {
      System.out.println("CSRF token from request attribute: " + csrfToken.getToken());
    }

    List<BoardDTO> boards = boardService.getAllUserBoards(userId);

    log.info("Successfully retrieved all boards for user with id: {}", userId);
    return new ResponseEntity<>(boards, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BoardDTO> getBoardById(@PathVariable Long id) {
    BoardDTO board = boardService.getBoardById(id);

    log.info("Successfully retrieved the board with id: {}", id);
    return new ResponseEntity<>(board, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<BoardDTO> createBoard(@Valid @RequestBody BoardCreationRequest request) {
    BoardDTO newBoardDTO = boardService.createBoard(request.getBoard(), request.getUserId());

    log.info("Successfully created a new board with id: {}", newBoardDTO.getId());
    return new ResponseEntity<>(newBoardDTO, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BoardDTO> updateBoardName(@Valid @RequestBody BoardDTO boardDTO,
                                            @PathVariable Long id) {
    BoardDTO updatedBoardDTO = boardService.updateBoardName(id, boardDTO);

    log.info("Successfully updated the board with id: {}", id);
    return new ResponseEntity<>(updatedBoardDTO, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BoardDTO> deleteBoard(@PathVariable Long id) {
    BoardDTO deletedBoard = boardService.deleteBoard(id);

    log.info("Successfully deleted the board with id: {}", id);
    return new ResponseEntity<>(deletedBoard, HttpStatus.NO_CONTENT);
  }
}
