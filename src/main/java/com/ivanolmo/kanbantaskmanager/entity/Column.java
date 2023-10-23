package com.ivanolmo.kanbantaskmanager.entity;

import com.ivanolmo.kanbantaskmanager.dto.TaskDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "columns")
@EntityListeners(AuditingEntityListener.class)
public class Column {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @jakarta.persistence.Column
  private String name;

  @CreatedDate
  @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @jakarta.persistence.Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;

  @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Task> tasks;

  public Column(String id) {
    this.id = id;
  }

  // custom Builder class for dto -> entity conversion
  public static class Builder {
    private final Column column = new Column();

    public Builder id(String id) {
      column.setId(id);
      return this;
    }

    public Builder name(String name) {
      column.setName(name);
      return this;
    }

    public Builder tasks(List<TaskDTO> taskDTOs) {
      List<Task> tasks = Optional.ofNullable(taskDTOs)
          .orElse(Collections.emptyList())
          .stream()
          .map(taskDTO -> Task
              .builder()
              .title(taskDTO.getTitle())
              .description(taskDTO.getDescription())
              .column(column)
              .build()).toList();

      column.setTasks(tasks);
      return this;
    }

    public Column build() {
      return column;
    }
  }
}
