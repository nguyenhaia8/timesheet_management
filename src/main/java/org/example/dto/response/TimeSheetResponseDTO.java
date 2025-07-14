package org.example.dto.response;

import java.time.LocalDate;

public record TimeSheetResponseDTO (
        Integer timesheetId,
        Integer employeeId,
        LocalDate weekStartDate,
        Double totalHours,
        String status,
        LocalDate submittedDate
) {
}
