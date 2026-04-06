package com.mamoki.beacon.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
