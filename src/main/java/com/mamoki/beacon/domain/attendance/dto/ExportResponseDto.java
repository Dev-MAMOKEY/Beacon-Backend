package com.mamoki.beacon.domain.attendance.dto;

import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;

public record ExportResponseDto(List<ExportItem> data) {
    public record ExportItem(
            String memberName,
            String stdId,
            String sessionName,
            LocalDate date,
            AttendanceStatus status,
            String adminNote
    ) {}
}