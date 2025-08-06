package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Approval")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approvalId")
    private Integer approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheetId", nullable = false)
    private TimeSheet timesheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approvedBy", nullable = false)
    private Employee approvedBy;

    @Column(name = "approvedAt")
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    // Constructors
    public Approval() {}

    public Approval(TimeSheet timesheet, Employee approvedBy) {
        this.timesheet = timesheet;
        this.approvedBy = approvedBy;
        this.status = ApprovalStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Integer approvalId) {
        this.approvalId = approvalId;
    }

    public TimeSheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(TimeSheet timesheet) {
        this.timesheet = timesheet;
    }

    public Employee getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Employee approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
