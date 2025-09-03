package com.company.task_manager.controller;

import com.company.task_manager.dto.*;
import com.company.task_manager.model.Task;
import com.company.task_manager.model.User;
import com.company.task_manager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@PreAuthorize("isAuthenticated()")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/board/{boardId}")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long boardId, @Valid @RequestBody TaskRequest taskRequest) {
        Task task = taskService.createTask(boardId, taskRequest);
        return ResponseEntity.ok(convertToResponse(task));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long taskId, @Valid @RequestBody TaskStatusUpdateRequest statusRequest) {
        Task task = taskService.updateTaskStatus(taskId, statusRequest.getStatus());
        return ResponseEntity.ok(convertToResponse(task));
    }

    @PostMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignUserToTask(@PathVariable Long taskId, @Valid @RequestBody TaskAssigneeRequest assigneeRequest) {
        Task task = taskService.assignUserToTask(taskId, assigneeRequest.getUserId());
        return ResponseEntity.ok(convertToResponse(task));
    }

    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskResponse> moveTask(@PathVariable Long taskId, @Valid @RequestBody TaskMoveRequest moveRequest) {
        Task task = taskService.moveTask(taskId, moveRequest.getNewBoardId());
        return ResponseEntity.ok(convertToResponse(task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully!"));
    }

    private TaskResponse convertToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getBoard().getId(),
                task.getAssignees().stream().map(User::getId).collect(Collectors.toSet())
        );
    }
}