package com.company.task_manager.service;

import com.company.task_manager.dto.TaskRequest;
import com.company.task_manager.model.Board;
import com.company.task_manager.model.Task;
import com.company.task_manager.model.TaskStatus;
import com.company.task_manager.model.User;
import com.company.task_manager.repository.BoardRepository;
import com.company.task_manager.repository.TaskRepository;
import com.company.task_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardService boardService;

    @Transactional
    public Task createTask(Long boardId, TaskRequest taskRequest) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + boardId));
        // Business Rule: Check if user is a member of the workspace containing the board.
        boardService.checkMembershipAndGetWorkspace(board.getWorkspace().getId());

        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(TaskStatus.TO_DO); // Default status
        task.setBoard(board);

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = getTaskAndCheckMembership(taskId);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Transactional
    public Task assignUserToTask(Long taskId, Long userId) {
        Task task = getTaskAndCheckMembership(taskId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Business Rule: You can only assign users who are also members of the workspace.
        boolean isMember = task.getBoard().getWorkspace().getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));

        if (!isMember) {
            throw new AccessDeniedException("Cannot assign a user who is not a member of the workspace");
        }

        task.getAssignees().add(user);
        return taskRepository.save(task);
    }

    @Transactional
    public Task moveTask(Long taskId, Long newBoardId){
        Task task = getTaskAndCheckMembership(taskId);
        Board newBoard = boardRepository.findById(newBoardId)
                .orElseThrow(() -> new RuntimeException("Destination board not found with id: " + newBoardId));

        if (!task.getBoard().getWorkspace().getId().equals(newBoard.getWorkspace().getId())) {
            throw new AccessDeniedException("Cannot move task to a board in a different workspace.");
        }

        task.setBoard(newBoard);

        String newBoardName = newBoard.getName().toUpperCase().replace(" ", "_");
        try {
            task.setStatus(TaskStatus.valueOf(newBoardName));
        }
        catch (IllegalArgumentException e) {
            task.setStatus(TaskStatus.TO_DO);
        }

        return taskRepository.save(task);
    }

    private Task getTaskAndCheckMembership(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        boardService.checkMembershipAndGetWorkspace(task.getBoard().getWorkspace().getId());
        return task;
    }

    public List<Task> getTasksByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + boardId));

        boardService.checkMembershipAndGetWorkspace(board.getWorkspace().getId());

        return taskRepository.findByBoardId(boardId);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        getTaskAndCheckMembership(taskId);
        taskRepository.deleteById(taskId);
    }
}
