package com.mamoki.beacon.domain.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record AttendanceCheckRequestDto(@NotBlank String otpCode) {
}
