package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
  @Query("SELECT s FROM Subtask s WHERE LOWER(s.title) = LOWER(:title) AND s.task.id = :taskId")
  Optional<Subtask> findByTitleAndTaskIAndId(@Param("title") String title,
                                                    @Param("taskId") Long taskId);
}
