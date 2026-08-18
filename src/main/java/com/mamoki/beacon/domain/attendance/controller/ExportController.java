package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.ExportResponseDto;
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
@RequestMapping("/api/v1/clubs/{clubId}/export")
@RequiredArgsConstructor
public class ExportController {
    private final AttendanceService attendanceService;

    @Operation(summary = "출석 데이터 내보내기", description = "기간 내 출석 데이터를 내보냅니다. memberId 입력 시 특정 멤버만 필터링합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "출석 데이터 조회 성공")
    @ApiAdminErrorResponse
    @GetMapping
    public ResponseEntity<RsData<ExportResponseDto>> getExportData(
            @AuthenticationPrincipal Long adminId,
            @PathVariable Long clubId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long memberId) {
        return ResponseEntity.ok(RsData.success(
                attendanceService.getExportData(adminId, clubId, startDate, endDate, memberId)));
    }
}