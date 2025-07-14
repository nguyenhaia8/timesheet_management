package org.example.dto.response;

public record TaskResponseDTO (
        Integer taskId,
        Integer projectId,
        String taskName,
        String description,
        Integer createdBy
) {
}
