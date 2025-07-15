package org.example.dto.response;

import java.time.LocalDate;

public record ApprovalResponseDTO (
        Integer approvalId,
        Integer timeSheetId,
        Integer approverId,
        String approvalStatus,
        LocalDate approvalDate,
        String comments
) {
}
