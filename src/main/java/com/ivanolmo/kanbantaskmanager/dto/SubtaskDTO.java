package com.ivanolmo.kanbantaskmanager.dto;

import com.ivanolmo.kanbantaskmanager.dto.validation.ValidSubtask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidSubtask
public class SubtaskDTO {
  private String id;

  private String title;

  @Builder.Default
  private Boolean completed = Boolean.FALSE;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
