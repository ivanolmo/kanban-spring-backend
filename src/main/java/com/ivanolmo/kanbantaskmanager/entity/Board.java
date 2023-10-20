package com.ivanolmo.kanbantaskmanager.entity;

import com.ivanolmo.kanbantaskmanager.dto.ColumnDTO;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Table(name = "boards")
@EntityListeners(AuditingEntityListener.class)
public class Board {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @jakarta.persistence.Column(nullable = false)
  private String name;

  @CreatedDate
  @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @jakarta.persistence.Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Column> columns = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Override
  public String toString() {
    return "Board [id=" + id + ", name=" + name + "]";
  }

  // custom Builder class for dto -> entity conversion
  public static class Builder {
    private final Board board = new Board();

    public Builder id(String id) {
      board.setId(id);
      return this;
    }

    public Builder name(String name) {
      board.setName(name);
      return this;
    }

    public Builder columns(List<ColumnDTO> columnDTOs) {
      List<Column> columns = Optional.ofNullable(columnDTOs)
          .orElse(Collections.emptyList())
          .stream()
          .map(columnDTO -> {
            Column column = new Column();
            column.setName(columnDTO.getName());
            column.setBoard(board);
            return column;
          })
          .toList();

      board.setColumns(columns);
      return this;
    }

    public Board build() {
      return board;
    }
  }
}
