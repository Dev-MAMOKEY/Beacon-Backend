package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.ExportResponseDto;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/clubs/{clubId}/export")
@RequiredArgsConstructor
public class ExportController {
    private final AttendanceService attendanceService;

    @GetMapping //출석 데이터 내보내기 api
    public ResponseEntity<RsData<ExportResponseDto>> getExportData(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long clubId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long memberId
    ) {
        return ResponseEntity.ok(RsData.success(
                attendanceService.getExportData(adminId, clubId, startDate, endDate, memberId)
        ));
    }
}