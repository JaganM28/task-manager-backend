package com.company.task_manager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskAssigneeRequest {

    @NotNull
    private Long userId;
}
