package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository extends JpaRepository<Column, Long> {
  List<Column> findAllByBoardId(Long boardId);

  @Query("SELECT c FROM Column c WHERE LOWER(c.name) = LOWER(:name) AND c.board.id = :boardId")
  Optional<Column> findByNameAndBoardId(@Param("name") String name,
                                              @Param("boardId") Long boardId);
}
