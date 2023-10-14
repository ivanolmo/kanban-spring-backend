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
@Table(name = "columns")
@EntityListeners(AuditingEntityListener.class)
public class Column {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Column name cannot be blank")
  @Size(min = 3, max = 50, message = "Column name should be between 3 and 50 characters")
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

  @OneToMany(mappedBy = "column")
  private List<Task> tasks;

  // custom Builder class for dto -> entity conversion
  public static class Builder {
    private final Column column = new Column();

    public Builder id(Long id) {
      column.setId(id);
      return this;
    }

    public Builder name(String name) {
      column.setName(name);
      return this;
    }

    public Column build() {
      return column;
    }
  }
}
