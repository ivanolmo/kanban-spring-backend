package com.ivanolmo.kanbantaskmanager.entity;

import com.ivanolmo.kanbantaskmanager.dto.SubtaskDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @jakarta.persistence.Column(nullable = false)
  private String title;

  @jakarta.persistence.Column(nullable = false)
  private String description;

  @CreatedDate
  @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @jakarta.persistence.Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "column_id", nullable = false)
  private Column column;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  @lombok.Builder.Default
  private List<Subtask> subtasks = new ArrayList<>();

  @Override
  public String toString() {
    return "Task [id=" + id + ", title=" + title + "]";
  }

  // custom Builder class for dto -> entity conversion
  public static class Builder {
    private final Task task = new Task();

    public Builder id(String id) {
      task.setId(id);
      return this;
    }

    public Builder title(String title) {
      task.setTitle(title);
      return this;
    }

    public Builder description(String description) {
      task.setDescription(description);
      return this;
    }

    public Builder subtasks(List<SubtaskDTO> subtaskDTOs) {
      List<Subtask> subtasks = Optional.ofNullable(subtaskDTOs)
          .orElse(Collections.emptyList())
          .stream()
          .map(subtaskDTO -> Subtask
              .builder()
              .title(subtaskDTO.getTitle())
              .completed(subtaskDTO.getCompleted())
              .task(task)
              .build()).toList();

      task.setSubtasks(subtasks);
      return this;
    }

    public Task build() {
      return task;
    }
  }
}
