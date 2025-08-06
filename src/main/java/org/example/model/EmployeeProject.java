package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "EmployeeProject")
public class EmployeeProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeeProjectId")
    private Integer employeeProjectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @Column(name = "assignedDate")
    private LocalDate assignedDate;

    @Column(name = "roleInProject")
    private String roleInProject;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    // Constructors
    public EmployeeProject() {}

    public EmployeeProject(Employee employee, Project project, String roleInProject) {
        this.employee = employee;
        this.project = project;
        this.roleInProject = roleInProject;
        this.assignedDate = LocalDate.now();
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getEmployeeProjectId() {
        return employeeProjectId;
    }

    public void setEmployeeProjectId(Integer employeeProjectId) {
        this.employeeProjectId = employeeProjectId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getRoleInProject() {
        return roleInProject;
    }

    public void setRoleInProject(String roleInProject) {
        this.roleInProject = roleInProject;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 