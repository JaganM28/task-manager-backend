package com.company.task_manager.service;

import com.company.task_manager.dto.WorkspaceRequest;
import com.company.task_manager.model.User;
import com.company.task_manager.model.Workspace;
import com.company.task_manager.repository.UserRepository;
import com.company.task_manager.repository.WorkspaceRepository;
import com.company.task_manager.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Workspace createWorkspace(WorkspaceRequest workspaceRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: Authenticated user not found in database."));

        Workspace workspace = new Workspace();
        workspace.setName(workspaceRequest.getName());
        workspace.setOwner(owner);
        workspace.setMembers(Set.of(owner));

        return workspaceRepository.save(workspace);
    }

    public List<Workspace> findMyWorkspaces() {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        return workspaceRepository.findByMembers_Id(userId);
    }

    @Transactional
    public void deleteWorkspace(Long workspaceId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + workspaceId));

        if (!workspace.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Only the workspace owner can delete it.");
        }

        workspaceRepository.delete(workspace);
    }

}
