package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.BoardCreationRequestDTO;
import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import com.ivanolmo.kanbantaskmanager.service.BoardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<ApiResponse<List<BoardDTO>>> getAllUserBoards(@RequestParam String userId) {
    List<BoardDTO> boards = boardService.getAllUserBoards(userId);

    log.info("Successfully retrieved all boards for user with id: {}", userId);
    return ApiResponseUtil.buildSuccessResponse(
        boards, "Successfully retrieved all boards for the user", HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BoardDTO>> getBoardById(@PathVariable String id) {
    BoardDTO board = boardService.getBoardById(id);

    log.info("Successfully retrieved the board with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(
        board, "Successfully retrieved the board by id", HttpStatus.OK);
  }

  @GetMapping("/{id}/columns")
  public ResponseEntity<ApiResponse<List<ColumnDTO>>> getAllColumnsForBoard(@PathVariable String id) {
    List<ColumnDTO> columns = boardService.getAllColumnsForBoard(id);

    log.info("Successfully retrieved all columns for board with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(
        columns, "Successfully retrieved all columns for the board", HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ApiResponse<BoardDTO>> addBoardToUser(@Valid @RequestBody BoardCreationRequestDTO request) {
    BoardDTO newBoardDTO = boardService.addBoardToUser(request.getUserId(), request.getBoard());

    log.info("Successfully created a new board with id: {}", newBoardDTO.getId());
    return ApiResponseUtil.buildSuccessResponse(
        newBoardDTO, "Successfully created the board", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BoardDTO>> updateBoardName(@Valid @RequestBody BoardDTO boardDTO,
                                                               @PathVariable String id) {
    BoardDTO updatedBoardDTO = boardService.updateBoardName(id, boardDTO);

    log.info("Successfully updated the board with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(
        updatedBoardDTO, "Successfully updated the board", HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable String id) {
    boardService.deleteBoard(id);

    log.info("Successfully deleted the board with id: {}", id);
    return ApiResponseUtil.buildSuccessResponse(null, null, HttpStatus.NO_CONTENT);
  }
}
