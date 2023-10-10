package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Optional<List<Board>> findByUserId(Long userId);
  Board findBoardByBoardNameAndUserId(String boardName, Long userId);
  Boolean existsByBoardNameAndUserIdAndIdNot(String boardName, Long userId, Long id);
}
