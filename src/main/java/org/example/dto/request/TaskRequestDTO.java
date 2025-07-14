package org.example.dto.request;

public record TaskRequestDTO (
    Integer projectId,
    String taskName,
    String description,
    Integer createdBy
){
}
