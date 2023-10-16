package com.ivanolmo.kanbantaskmanager.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubtaskDTO {
  private Long id;

  @NotBlank(message = "Subtask title cannot be blank")
  @Size(min = 3, max = 50, message = "Subtask title should be between 3 and 50 characters")
  private String title;

  private Optional<Boolean> completed = Optional.of(false);
}
