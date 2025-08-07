package org.example.dto.response;

public record UserResponseDTO (
        Integer employeeId,
        String employeeFirstName,
        String employeeLastName,
        String employeeEmail,
        Integer managerId
) {
}
