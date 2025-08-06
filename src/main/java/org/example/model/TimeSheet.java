package org.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Timesheet")
public class TimeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timesheetId")
    private Integer timesheetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    @Column(name = "periodStartDate", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "periodEndDate", nullable = false)
    private LocalDate periodEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TimeSheetStatus status = TimeSheetStatus.DRAFT;

    @Column(name = "submissionDate")
    private LocalDateTime submissionDate;

    @Column(name = "totalHours", precision = 5, scale = 2)
    private BigDecimal totalHours = BigDecimal.ZERO;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    public enum TimeSheetStatus {
        DRAFT,
        SUBMITTED,
        APPROVED,
        REJECTED
    }

    // Constructors
    public TimeSheet() {}

    public TimeSheet(Employee employee, LocalDate periodStartDate, LocalDate periodEndDate) {
        this.employee = employee;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        this.status = TimeSheetStatus.DRAFT;
        this.totalHours = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Integer timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public TimeSheetStatus getStatus() {
        return status;
    }

    public void setStatus(TimeSheetStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
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
