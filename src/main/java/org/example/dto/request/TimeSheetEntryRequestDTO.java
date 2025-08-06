package org.example.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimeSheetEntryRequestDTO(
    Integer timesheetId,
    LocalDate date,
    Integer projectId,
    String taskDescription,
    BigDecimal hoursWorked
) {}
