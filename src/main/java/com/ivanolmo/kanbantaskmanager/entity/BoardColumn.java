package com.ivanolmo.kanbantaskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "board_columns")
@EntityListeners(AuditingEntityListener.class)
public class BoardColumn {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Column name cannot be blank")
  @Size(min = 3, max = 50, message = "Column name should be between 3 and 50 characters")
  @Column(name = "column_name")
  private String columnName;

  @ManyToOne
  @JoinColumn(name = "board_id")
  private Board board;

  @OneToMany(mappedBy = "boardColumn")
  private List<Task> tasks;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // custom Builder class for dto -> entity conversion
  public static class Builder {
    private final BoardColumn boardColumn = new BoardColumn();

    public Builder id(Long id) {
      boardColumn.setId(id);
      return this;
    }

    public Builder boardColumnName(String boardColumnName) {
      boardColumn.setColumnName(boardColumnName);
      return this;
    }

    public BoardColumn build() {
      return boardColumn;
    }
  }
}
