package com.company.task_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {

    private Long id;
    private String name;
    private Long workspaceId;
    private List<TaskResponse> tasks; // Including tasks when fetching a full board
}