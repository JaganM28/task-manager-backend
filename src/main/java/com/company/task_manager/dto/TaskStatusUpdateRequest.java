package com.company.task_manager.dto;

import com.company.task_manager.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {

    @NotNull
    private TaskStatus status;
}