package com.mamoki.beacon.domain.attendance.dto;

public record AttendanceRateResponse(Long memberId, Long clubId, long totalSessions, long attendedCount, double rate) {
}
