package org.example.dto.response;

import java.time.LocalDate;

public record TimeSheetEntryResponseDTO (
        Integer entryId,
        Integer timeSheetId,
        Integer taskId,
        LocalDate workDate,
        Double hoursWorked,
        String description
) {
}
