package com.ivanolmo.kanbantaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {
  private String id;

  @NotBlank(message = "Board name cannot be blank")
  @Size(min = 3, max = 50, message = "Board name should be between 3 and 50 characters")
  private String name;

  private List<ColumnDTO> columns;
}
