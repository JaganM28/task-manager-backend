package com.company.task_manager.repository;

import com.company.task_manager.model.User;
import com.company.task_manager.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByMembers_Id(Long userId);
}
