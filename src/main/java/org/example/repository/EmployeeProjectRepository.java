package org.example.repository;

import org.example.model.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Integer> {
    List<EmployeeProject> findByEmployeeEmployeeId(Integer employeeId);
    List<EmployeeProject> findByProjectProjectId(Integer projectId);
    List<EmployeeProject> findByEmployeeEmployeeIdAndIsActiveTrue(Integer employeeId);
} 