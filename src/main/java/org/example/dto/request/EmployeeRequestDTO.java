package org.example.dto.request;

public record EmployeeRequestDTO(
    String firstName,
    String lastName,
    String email,
    String position,
    Integer departmentId,
    Integer managerId
) {}
