package com.ivanolmo.kanbantaskmanager.dto;

import com.ivanolmo.kanbantaskmanager.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskInfo {
  private String taskId;
  private String userId;
  private Task task;
}
