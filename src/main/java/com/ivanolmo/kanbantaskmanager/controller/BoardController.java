package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.BoardCreationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
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
  public ResponseEntity<List<BoardDTO>> getAllUserBoards(@RequestParam String userId,
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
  public ResponseEntity<BoardDTO> getBoardById(@PathVariable String id) {
    BoardDTO board = boardService.getBoardById(id);

    log.info("Successfully retrieved the board with id: {}", id);
    return new ResponseEntity<>(board, HttpStatus.OK);
  }

  @GetMapping("/{id}/columns")
  public ResponseEntity<List<ColumnDTO>> getAllColumnsForBoard(@PathVariable String id) {
    List<ColumnDTO> columns = boardService.getAllColumnsForBoard(id);

    log.info("Successfully retrieved all columns for board with id: {}", id);
    return new ResponseEntity<>(columns, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<BoardDTO> addBoardToUser(@Valid @RequestBody BoardCreationRequestDTO request) {
    BoardDTO newBoardDTO = boardService.addBoardToUser(request.getUserId(), request.getBoard());

    log.info("Successfully created a new board with id: {}", newBoardDTO.getId());
    return new ResponseEntity<>(newBoardDTO, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BoardDTO> updateBoardName(@Valid @RequestBody BoardDTO boardDTO,
                                                  @PathVariable String id) {
    BoardDTO updatedBoardDTO = boardService.updateBoardName(id, boardDTO);

    log.info("Successfully updated the board with id: {}", id);
    return new ResponseEntity<>(updatedBoardDTO, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBoard(@PathVariable String id) {
    boardService.deleteBoard(id);

    log.info("Successfully deleted the board with id: {}", id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
