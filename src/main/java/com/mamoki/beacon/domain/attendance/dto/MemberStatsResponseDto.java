package com.mamoki.beacon.domain.attendance.dto;

import java.util.List;

public record MemberStatsResponseDto(List<MemberStatItem> members) {
    public record MemberStatItem(Long memberId, String name, int stdId, long totalSessions, long attendedCount, double attendanceRate){}
}
