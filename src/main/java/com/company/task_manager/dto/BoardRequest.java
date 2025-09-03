package com.company.task_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
}