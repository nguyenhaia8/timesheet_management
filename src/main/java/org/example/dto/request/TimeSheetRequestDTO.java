package org.example.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimeSheetRequestDTO(
    Integer employeeId,
    LocalDate periodStartDate,
    LocalDate periodEndDate,
    String status,
    BigDecimal totalHours
) {}
