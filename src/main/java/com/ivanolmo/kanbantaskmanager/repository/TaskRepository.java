package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.dto.TaskInfo;
import com.ivanolmo.kanbantaskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {
  @Query("SELECT t FROM Task t WHERE LOWER(t.title) = LOWER(:title) AND t.column.id = :columnId")
  Optional<Task> findByTitleAndColumnId(@Param("title") String title,
                                        @Param("columnId") String columnId);

  @Query("SELECT new com.ivanolmo.kanbantaskmanager.dto.TaskInfo(t.id, u.id, t) FROM Task t " +
      "JOIN t.column c JOIN c.board b JOIN b.user u WHERE t.id = :taskId")
  Optional<TaskInfo> findTaskInfoById(@Param("taskId") String taskId);
}
