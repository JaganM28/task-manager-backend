package com.company.task_manager.controller;

import com.company.task_manager.dto.CommentRequest;
import com.company.task_manager.dto.CommentResponse;
import com.company.task_manager.model.Comment;
import com.company.task_manager.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@PreAuthorize("isAuthenticated()")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long taskId, @Valid @RequestBody CommentRequest commentRequest) {
        Comment comment = commentService.addCommentToTask(taskId, commentRequest);
        return ResponseEntity.ok(convertToResponse(comment));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long taskId) {
        List<Comment> comments = commentService.getCommentsForTask(taskId);
        List<CommentResponse> response = comments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getTask().getId()
        );
    }
}