package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.MemberStatsResponseDto;
import com.mamoki.beacon.domain.attendance.dto.TrendResponseDto;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/clubs/{clubId}/stats")
@RequiredArgsConstructor
public class StatsController {
    private final AttendanceService attendanceService;

    @GetMapping("/trend")
    public ResponseEntity<RsData<TrendResponseDto>> getTrend(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long clubId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) { //iso는 2021-01-01 이런식으로 값을 받는걸 말한다고 함
        return ResponseEntity.ok(RsData.success(attendanceService.getTrend(adminId, clubId, startDate, endDate)));
    }

    @GetMapping("/members")
    public ResponseEntity<RsData<MemberStatsResponseDto>> getMemberStats(@AuthenticationPrincipal Long adminId, @PathVariable Long clubId) {
        return ResponseEntity.ok(RsData.success(attendanceService.getMemberStats(adminId, clubId)));
    }
}
