package com.company.task_manager.controller;

import com.company.task_manager.dto.MessageResponse;
import com.company.task_manager.dto.WorkspaceRequest;
import com.company.task_manager.dto.WorkspaceResponse;
import com.company.task_manager.model.Workspace;
import com.company.task_manager.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workspaces")
@PreAuthorize("isAuthenticated()")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody WorkspaceRequest workspaceRequest) {
        Workspace workspace = workspaceService.createWorkspace(workspaceRequest);
        WorkspaceResponse response = new WorkspaceResponse(workspace.getId(), workspace.getName(), workspace.getOwner().getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces() {
        List<Workspace> workspaces = workspaceService.findMyWorkspaces();
        List<WorkspaceResponse> response = workspaces.stream()
                .map(ws -> new WorkspaceResponse(ws.getId(), ws.getName(), ws.getOwner().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<?> deleteWorkspace(@PathVariable Long workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.ok(new MessageResponse("Workspace deleted successfully!"));
    }
}
