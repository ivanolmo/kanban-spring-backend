package com.ivanolmo.kanbantaskmanager.entity;

import jakarta.persistence.*;
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
}
