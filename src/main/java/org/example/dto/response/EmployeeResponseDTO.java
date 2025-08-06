package org.example.dto.response;

public record EmployeeResponseDTO(
    Integer employeeId,
    String firstName,
    String lastName,
    String email,
    String position,
    Integer departmentId,
    String departmentName,
    Integer managerId,
    String managerName
) {}
