package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.dto.BoardDTO;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {
  private final UserMapper userMapper;

  public BoardMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }
  public BoardDTO toDTO(Board board) {
    BoardDTO boardDTO = new BoardDTO();

    boardDTO.setId(board.getId());
    boardDTO.setBoardName(board.getBoardName());
    boardDTO.setUser(userMapper.toDTO(board.getUser()));

    return boardDTO;
  }

  public Board toEntity(BoardDTO boardDTO) {
    Board board = new Board();

    board.setId(boardDTO.getId());
    board.setBoardName(boardDTO.getBoardName());

    return board;
  }
}
