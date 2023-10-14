package com.ivanolmo.kanbantaskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @jakarta.persistence.Column(unique = true)
  private String email;

  @jakarta.persistence.Column(name = "email_verified")
  private LocalDateTime emailVerified;

  private String image;

  @CreatedDate
  @jakarta.persistence.Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @jakarta.persistence.Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Board> boards = new ArrayList<>();

  // spring security
  private String password;

  @Override
  public String toString() {
    return "User [id=" + id + ", name=" + name + "]";
  }
}
