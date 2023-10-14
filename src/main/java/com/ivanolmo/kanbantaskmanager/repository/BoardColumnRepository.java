package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
  List<BoardColumn> findAllColumnsByBoardId(Long boardId);
  @Query("SELECT c FROM BoardColumn c WHERE LOWER(c.columnName) = LOWER(:columnName) AND c.board" +
      ".id = :boardId")
  Optional<BoardColumn> findBoardColumnByColumnNameAndBoardId(@Param("columnName") String columnName,
                                                              @Param("boardId") Long boardId);
}
