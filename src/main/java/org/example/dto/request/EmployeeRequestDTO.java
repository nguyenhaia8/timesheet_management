package org.example.dto.request;

public record EmployeeRequestDTO(
        String firstName,
        String lastName,
        String email,
        Integer departmentId,
        Integer managerId
) {
}
