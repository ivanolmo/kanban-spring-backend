package com.ivanolmo.kanbantaskmanager.controller;

import com.ivanolmo.kanbantaskmanager.dto.BoardDTO;
import com.ivanolmo.kanbantaskmanager.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
  private final BoardService boardService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<BoardDTO>>> getAllUserBoards() {
    List<BoardDTO> boards = boardService.getAllUserBoards();

    log.info("Successfully retrieved all boards for the user");
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

  @PostMapping
  public ResponseEntity<ApiResponse<BoardDTO>> addBoardToUser(@Valid @RequestBody BoardDTO boardDTO) {
    BoardDTO newBoardDTO = boardService.addBoardToUser(boardDTO);

    log.info("Successfully created a new board with id: {}", newBoardDTO.getId());
    return ApiResponseUtil.buildSuccessResponse(
        newBoardDTO, "Successfully created the board", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BoardDTO>> updateBoardName(@Valid @RequestBody String newName,
                                                               @PathVariable String id) {
    BoardDTO updatedBoardDTO = boardService.updateBoardName(id, newName);

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
