package com.mamoki.beacon.domain.club.dto;

import java.time.LocalDateTime;

public record ClubResponseDto(
        Long id,
        String clubName,
        String clubDescription,
        LocalDateTime createdAt
) {
}
