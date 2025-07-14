package org.example.dto.request;

public record EmployeeRequestDTO(
        String employeeCode,
        String firstName,
        String lastName,
        String department
) {

}
