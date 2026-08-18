package com.mamoki.beacon.domain.attendance.dto;

import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;

import java.time.LocalDateTime;

//SSE 실시간 출석 피드 이벤트 (누가, 어떤 상태로, 언제 출석했는지)
public record AttendanceFeedDto(
        Long memberId,
        String memberName,
        String stdId,
        Long sessionId,
        String sessionName,
        AttendanceStatus status,
        LocalDateTime checkedAt
) {
}
