package org.example.dto.response;

public record DepartmentResponseDTO(
    Integer departmentId,
    String name,
    Integer headEmployeeId,
    String headEmployeeName
) {}
