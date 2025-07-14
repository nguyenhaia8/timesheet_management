package org.example.dto.request;

import java.time.LocalDate;

public record TimeSheetEntryRequestDTO (
    Integer timeSheetId,
    Integer taskId,
    LocalDate workDate,
    Double hoursWorked,
    String description
){
}
