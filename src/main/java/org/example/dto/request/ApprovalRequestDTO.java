package org.example.dto.request;

import java.time.LocalDate;

public record ApprovalRequestDTO (
    Integer timeSheetId,
    Integer approverId,
    String approvalStatus,
    LocalDate approvalDate,
    String comments
){
}
