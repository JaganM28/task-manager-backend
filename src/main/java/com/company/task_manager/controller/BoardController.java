package com.company.task_manager.controller;

import com.company.task_manager.dto.BoardRequest;
import com.company.task_manager.dto.BoardResponse;
import com.company.task_manager.dto.MessageResponse;
import com.company.task_manager.dto.TaskResponse;
import com.company.task_manager.model.Board;
import com.company.task_manager.model.Task;
import com.company.task_manager.model.User;
import com.company.task_manager.service.BoardService;
import com.company.task_manager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/boards")
@PreAuthorize("isAuthenticated()")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@PathVariable Long workspaceId, @Valid @RequestBody BoardRequest boardRequest) {
        Board board = boardService.createBoard(workspaceId, boardRequest);
        BoardResponse response = new BoardResponse(board.getId(), board.getName(), board.getWorkspace().getId(), List.of());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoardsForWorkspace(@PathVariable Long workspaceId) {
        List<Board> boards = boardService.getBoardsForWorkspace(workspaceId);
        List<BoardResponse> response = boards.stream()
                .map(b -> new BoardResponse(b.getId(), b.getName(), b.getWorkspace().getId(), List.of()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Autowired
    private TaskService taskService;

    @GetMapping("/{boardId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksForBoard(@PathVariable Long boardId) {
        List<Task> tasks = taskService.getTasksByBoard(boardId);
        List<TaskResponse> response = tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long workspaceId, @PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(new MessageResponse("Board deleted successfully!"));
    }

    private TaskResponse convertToTaskResponse(Task task) {
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
