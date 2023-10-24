package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskInfo;
import com.ivanolmo.kanbantaskmanager.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask, String> {
  @Query("SELECT s FROM Subtask s WHERE LOWER(s.title) = LOWER(:title) AND s.task.id = :taskId")
  Optional<Subtask> findByTitleAndTaskId(@Param("title") String title,
                                         @Param("taskId") String taskId);

  @Query("SELECT new com.ivanolmo.kanbantaskmanager.dto.SubtaskInfo(st.id, u.id, st) FROM Subtask" +
      " st JOIN st.task t JOIN t.column c JOIN c.board b JOIN b.user u WHERE st.id = :subtaskId")
  Optional<SubtaskInfo> findSubtaskInfoById(@Param("subtaskID") String subtaskId);
}
