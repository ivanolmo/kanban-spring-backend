package com.ivanolmo.kanbantaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColumnDTO {
  private String id;

  @NotBlank(message = "Column name cannot be blank")
  @Size(min = 3, max = 50, message = "Column name should be between 3 and 50 characters")
  private String name;

  @NotBlank(message = "Column color cannot be blank")
  @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}){1,2}$", message = "Invalid column color code")
  private String color;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private List<TaskDTO> tasks;
}
