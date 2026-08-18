package com.mamoki.beacon.domain.auth.dto;

public record SignupResponse (
        Long id,
        String studentId,
        String name
) {
}
