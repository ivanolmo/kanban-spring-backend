package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.dto.ColumnInfo;
import com.ivanolmo.kanbantaskmanager.entity.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ColumnRepository extends JpaRepository<Column, String> {
  @Query("SELECT c FROM Column c WHERE LOWER(c.name) = LOWER(:name) AND c.board.id = :boardId")
  Optional<Column> findByNameAndBoardId(@Param("name") String name,
                                        @Param("boardId") String boardId);

  @Query("SELECT new com.ivanolmo.kanbantaskmanager.dto.ColumnInfo(c.id, u.id, c) FROM Column c " +
      "JOIN c.board b JOIN b.user u WHERE c.id = :columnId")
  Optional<ColumnInfo> findColumnInfoById(@Param("columnId") String columnId);
}
