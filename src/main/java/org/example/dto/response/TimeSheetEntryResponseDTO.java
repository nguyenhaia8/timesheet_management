package org.example.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimeSheetEntryResponseDTO(
    Integer entryId,
    Integer timesheetId,
    LocalDate date,
    Integer projectId,
    String projectName,
    String taskDescription,
    BigDecimal hoursWorked
) {}
