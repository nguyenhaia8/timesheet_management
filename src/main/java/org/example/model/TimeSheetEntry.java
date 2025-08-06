package org.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TimesheetEntry")
public class TimeSheetEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entryId")
    private Integer entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheetId", nullable = false)
    private TimeSheet timesheet;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

    @Column(name = "taskDescription", columnDefinition = "TEXT", nullable = false)
    private String taskDescription;

    @Column(name = "hoursWorked", precision = 4, scale = 2, nullable = false)
    private BigDecimal hoursWorked;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    // Constructors
    public TimeSheetEntry() {}

    public TimeSheetEntry(TimeSheet timesheet, LocalDate date, Project project, 
                         String taskDescription, BigDecimal hoursWorked) {
        this.timesheet = timesheet;
        this.date = date;
        this.project = project;
        this.taskDescription = taskDescription;
        this.hoursWorked = hoursWorked;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public TimeSheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(TimeSheet timesheet) {
        this.timesheet = timesheet;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(BigDecimal hoursWorked) {
        this.hoursWorked = hoursWorked;
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
