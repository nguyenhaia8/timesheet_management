package org.example.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TimeSheetResponseDTO(
    Integer timesheetId,
    Integer employeeId,
    String employeeName,
    LocalDate periodStartDate,
    LocalDate periodEndDate,
    String status,
    LocalDateTime submissionDate,
    BigDecimal totalHours
) {}
