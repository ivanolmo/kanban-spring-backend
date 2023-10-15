package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findAllByColumnId(Long columnId);

  @Query("SELECT t FROM Task t WHERE LOWER(t.title) = LOWER(:title) AND t.column.id = :columnId")
  Optional<Task> findTaskByTitleAndColumnId(@Param("title") String title,
                                            @Param("columnId") Long columnId);
}
