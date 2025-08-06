package org.example.dto.response;

import java.time.LocalDateTime;

public record ApprovalResponseDTO(
    Integer approvalId,
    Integer timesheetId,
    Integer approvedBy,
    String approvedByName,
    LocalDateTime approvedAt,
    String status,
    String comments
) {}
