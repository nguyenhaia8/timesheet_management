package org.example.dto.request;

import java.time.LocalDate;

public record ProjectRequestDTO(
    String name,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    Integer clientId,
    Integer projectManagerId,
    String status
) {}
