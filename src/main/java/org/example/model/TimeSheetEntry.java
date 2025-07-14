package org.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Timesheet_Entry")
public class TimeSheetEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Integer entryId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheet_id", nullable = false)
    private TimeSheet timeSheet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;
    
    @Column(name = "hours_worked", nullable = false, precision = 4, scale = 2)
    private BigDecimal hoursWorked;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // Default constructor
    public TimeSheetEntry() {}
    
    // Constructor with all fields
    public TimeSheetEntry(TimeSheet timeSheet, Task task, LocalDate workDate, BigDecimal hoursWorked, String description) {
        this.timeSheet = timeSheet;
        this.task = task;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.description = description;
    }
    
    // Getters and Setters
    public Integer getEntryId() {
        return entryId;
    }
    
    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }
    
    public TimeSheet getTimeSheet() {
        return timeSheet;
    }
    
    public void setTimeSheet(TimeSheet timeSheet) {
        this.timeSheet = timeSheet;
    }
    
    public Task getTask() {
        return task;
    }
    
    public void setTask(Task task) {
        this.task = task;
    }
    
    public LocalDate getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }
    
    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }
    
    public void setHoursWorked(BigDecimal hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "TimeSheetEntry{" +
                "entryId=" + entryId +
                ", timeSheet=" + (timeSheet != null ? timeSheet.getTimesheetId() : null) +
                ", task=" + (task != null ? task.getTaskId() : null) +
                ", workDate=" + workDate +
                ", hoursWorked=" + hoursWorked +
                ", description='" + description + '\'' +
                '}';
    }
}
