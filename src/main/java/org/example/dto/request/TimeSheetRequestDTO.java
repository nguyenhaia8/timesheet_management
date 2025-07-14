package org.example.dto.request;

import java.time.LocalDate;

public record TimeSheetRequestDTO (
    Integer employeeId,
    LocalDate weekStartDate,
    Double totalHours,
    String status,
    LocalDate submittedDate
){
}
