package com.mamoki.beacon.domain.auth.dto;

public record SignupResponse (
        Long id,
        int studentId,
        String name
) {
}
