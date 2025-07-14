package org.example.dto.response;

public record DepartmentResponseDTO (
        Integer departmentId,
        String departmentName,
        String description) {
}
