package com.company.task_manager.service;

import com.company.task_manager.dto.BoardRequest;
import com.company.task_manager.model.Board;
import com.company.task_manager.model.Workspace;
import com.company.task_manager.repository.BoardRepository;
import com.company.task_manager.repository.WorkspaceRepository;
import com.company.task_manager.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Transactional
    public Board createBoard(Long workspaceId, BoardRequest boardRequest) {
        Workspace workspace = checkMembershipAndGetWorkspace(workspaceId);

        Board board = new Board();
        board.setName(boardRequest.getName());
        board.setWorkspace(workspace);

        return boardRepository.save(board);
    }

    public List<Board> getBoardsForWorkspace(Long workspaceId) {
        checkMembershipAndGetWorkspace(workspaceId);
        return boardRepository.findByWorkspaceId(workspaceId);
    }

    public Workspace checkMembershipAndGetWorkspace(Long workspaceId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + workspaceId));

        boolean isMember = workspace.getMembers().stream().anyMatch(member -> member.getId().equals(userId));
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of this workspace");
        }
        return workspace;
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        checkMembershipAndGetWorkspace(board.getWorkspace().getId());
        boardRepository.deleteById(boardId);
    }
}
