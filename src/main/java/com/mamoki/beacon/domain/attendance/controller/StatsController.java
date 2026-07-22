package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.DistributionResponseDto;
import com.mamoki.beacon.domain.attendance.dto.MemberStatsResponseDto;
import com.mamoki.beacon.domain.attendance.dto.SummaryResponseDto;
import com.mamoki.beacon.domain.attendance.dto.TrendResponseDto;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiAdminErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Stats", description = "출석 통계 API")
@RestController
@RequestMapping("/api/v1/clubs/{clubId}/stats")
@RequiredArgsConstructor
public class StatsController {
    private final AttendanceService attendanceService;

    @Operation(summary = "출석 트렌드 조회", description = "기간별 세션 출석률 추이를 조회합니다. startDate ~ endDate 범위의 세션 데이터를 반환합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "출석 트렌드 조회 성공")
    @ApiAdminErrorResponse
    @GetMapping("/trend")
    public ResponseEntity<RsData<TrendResponseDto>> getTrend(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long clubId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(RsData.success(attendanceService.getTrend(adminId, clubId, startDate, endDate)));
    }

    @Operation(summary = "멤버별 출석률 조회", description = "동아리 전체 멤버의 출석률을 조회합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "멤버별 출석률 조회 성공")
    @ApiAdminErrorResponse
    @GetMapping("/members")
    public ResponseEntity<RsData<MemberStatsResponseDto>> getMemberStats(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long clubId) {
        return ResponseEntity.ok(RsData.success(attendanceService.getMemberStats(adminId, clubId)));
    }

    @Operation(summary = "대시보드 요약 조회", description = "오늘 출석/지각 수, 전체 멤버 수, 평균 출석률을 조회합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "대시보드 요약 조회 성공")
    @ApiAdminErrorResponse
    @GetMapping("/summary")
    public ResponseEntity<RsData<SummaryResponseDto>> getSummary(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long clubId) {
        return ResponseEntity.ok(RsData.success(attendanceService.getSummary(adminId, clubId)));
    }

    @Operation(summary = "출석 상태별 분포 조회", description = "기간 내 PRESENT / LATE / ABSENT / ETC 상태별 건수와 비율을 조회합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "출석 분포 조회 성공")
    @ApiAdminErrorResponse
    @GetMapping("/distribution")
    public ResponseEntity<RsData<DistributionResponseDto>> getDistribution(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long clubId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(RsData.success(attendanceService.getDistribution(adminId, clubId, startDate, endDate)));
    }
}