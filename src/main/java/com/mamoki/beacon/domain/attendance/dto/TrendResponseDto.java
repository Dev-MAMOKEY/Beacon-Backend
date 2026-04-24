package com.mamoki.beacon.domain.attendance.dto;

import java.time.LocalDate;
import java.util.List;

public record TrendResponseDto(List<TrendItem> trend) { //세션별 출석률을 담기위한 list
    public record TrendItem( //세션 하나의 출석률을 위한 record
            Long sessionId,
            String sessionName,
            LocalDate date,
            long total,
            long attended,
            double attendanceRate
    ) {}
}