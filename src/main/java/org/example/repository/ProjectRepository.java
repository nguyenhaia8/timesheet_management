package org.example.repository;

import org.example.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByClientClientId(Integer clientId);
    List<Project> findByProjectManagerEmployeeId(Integer projectManagerId);
    List<Project> findByStatus(Project.ProjectStatus status);
} 