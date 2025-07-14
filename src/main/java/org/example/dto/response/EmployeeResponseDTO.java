package org.example.dto.response;

public record EmployeeResponseDTO(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String department
) {

}
