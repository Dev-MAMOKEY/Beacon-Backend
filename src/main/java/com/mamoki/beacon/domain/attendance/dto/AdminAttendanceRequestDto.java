package com.mamoki.beacon.domain.attendance.dto;

import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record AdminAttendanceRequestDto(@NotNull Long memberId, @NotNull AttendanceStatus attendanceStatus, String adminNote) {
}
