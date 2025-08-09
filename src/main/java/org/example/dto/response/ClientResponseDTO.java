package org.example.dto.response;

public record ClientResponseDTO(
    Integer clientId,
    String clientName,
    String contactEmail,
    String contactPhone,
    String address
) {}
