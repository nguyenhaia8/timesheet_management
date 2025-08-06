package org.example.dto.response;

import java.time.LocalDate;

public record ProjectResponseDTO(
    Integer projectId,
    String name,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    Integer clientId,
    String clientName,
    Integer projectManagerId,
    String projectManagerName,
    String status
) {}
