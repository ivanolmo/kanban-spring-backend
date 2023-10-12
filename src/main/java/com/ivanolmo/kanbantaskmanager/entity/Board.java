package com.ivanolmo.kanbantaskmanager.entity;

import com.ivanolmo.kanbantaskmanager.entity.dto.ColumnDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "boards")
@EntityListeners(AuditingEntityListener.class)
public class Board {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Board name cannot be blank")
  @Size(min = 3, max = 50, message = "Board name should be between 3 and 50 characters")
  @Column(nullable = false)
  private String boardName;

  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BoardColumn> boardColumns = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Override
  public String toString() {
    return "Board [id=" + id + ", boardName=" + boardName + "]";
  }

  // custom Builder class for dto -> entity conversion
  public static class Builder {
    private final Board board = new Board();

    public Builder id(Long id) {
      board.setId(id);
      return this;
    }

    public Builder boardName(String boardName) {
      board.setBoardName(boardName);
      return this;
    }

    public Builder columns(List<ColumnDTO> columnDTOs) {
      List<BoardColumn> columns = columnDTOs.stream()
          .map(columnDTO -> {
            BoardColumn column = new BoardColumn();
            column.setColumnName(columnDTO.getColumnName());
            column.setBoard(board);
            return column;
          })
          .toList();

      board.setBoardColumns(columns);
      return this;
    }

    public Board build() {
      return board;
    }
  }
}
