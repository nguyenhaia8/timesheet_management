package org.example.dto.response;

public record UserResponseDTO (
        Integer userId,
        String username,
        Integer employeeId,
        String employeeFirstName,
        String employeeLastName,
        String employeeEmail
) {
}
