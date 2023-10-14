package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Optional<List<Board>> findByUserId(Long userId);

  @Query("SELECT b FROM Board b WHERE LOWER(b.name) = LOWER(:name) AND b.user.id = :userId")
  Optional<Board> findBoardByBoardNameAndUserId(@Param("name") String name, @Param("userId") Long userId);
}
