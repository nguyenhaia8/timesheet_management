package org.example.dto.request;

public record ApprovalRequestDTO(
    Integer timesheetId,
    Integer approvedBy,
    String status,
    String comments
) {}
