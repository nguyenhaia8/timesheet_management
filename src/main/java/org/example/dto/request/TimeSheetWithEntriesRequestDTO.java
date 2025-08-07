package org.example.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TimeSheetWithEntriesRequestDTO(
    Integer employeeId,
    LocalDate periodStartDate,
    LocalDate periodEndDate,
    String status,
    LocalDateTime submissionDate,
    BigDecimal totalHours,
    List<TimeSheetEntryRequestDTO> timeSheetEntries
) {} 