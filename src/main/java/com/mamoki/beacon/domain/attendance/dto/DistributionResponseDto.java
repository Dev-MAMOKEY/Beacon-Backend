package com.mamoki.beacon.domain.attendance.dto;

public record DistributionResponseDto(long total, long present, double presentRate, long late, double lateRate, long absent, double absentRate, long etc, double etcRate) {
}
