package com.ivanolmo.kanbantaskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "subtasks")
@EntityListeners(AuditingEntityListener.class)
public class Subtask {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @jakarta.persistence.Column(name = "title", nullable = false)
  private String title;

  @jakarta.persistence.Column(name = "completed", nullable = false)
  private Boolean completed = false;

  @CreatedDate
  @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @jakarta.persistence.Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  public static class Builder {
    private final Subtask subtask = new Subtask();

    public Builder id(String id) {
      subtask.setId(id);
      return this;
    }

    public Builder title(String title) {
      subtask.setTitle(title);
      return this;
    }

    public Builder completed(Boolean completed) {
      subtask.setCompleted(completed);
      return this;
    }

    public Subtask build() {
      return subtask;
    }
  }
}
