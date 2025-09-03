package com.company.task_manager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskMoveRequest {
    @NotNull
    private Long newBoardId;
}
