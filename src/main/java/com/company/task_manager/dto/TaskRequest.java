package com.company.task_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequest {

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    private String description;
}