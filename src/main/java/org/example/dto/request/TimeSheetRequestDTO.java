package org.example.dto.request;

import java.time.LocalDate;

public record TimeSheetRequestDTO(
    Integer employeeId,
    LocalDate periodStartDate,
    LocalDate periodEndDate
) {}
