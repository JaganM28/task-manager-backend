package com.company.task_manager.service;

import com.company.task_manager.dto.CommentRequest;
import com.company.task_manager.model.Comment;
import com.company.task_manager.model.Task;
import com.company.task_manager.model.User;
import com.company.task_manager.repository.CommentRepository;
import com.company.task_manager.repository.TaskRepository;
import com.company.task_manager.repository.UserRepository;
import com.company.task_manager.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardService boardService; // Reusing membership check logic

    @Transactional
    public Comment addCommentToTask(Long taskId, CommentRequest commentRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        // Business Rule: A user can only comment on a task if they are part of the workspace.
        boardService.checkMembershipAndGetWorkspace(task.getBoard().getWorkspace().getId());

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User author = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Author not found."));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setTask(task);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsForTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        // Business Rule: Check membership before allowing to view comments.
        boardService.checkMembershipAndGetWorkspace(task.getBoard().getWorkspace().getId());

        return commentRepository.findByTaskId(taskId);
    }
}