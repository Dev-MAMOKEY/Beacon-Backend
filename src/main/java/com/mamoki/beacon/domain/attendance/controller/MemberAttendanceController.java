package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.MyAttendanceRecordDto;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiMemberErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Attendance", description = "출석 API")
@RestController
@RequestMapping("/api/v1/clubs/{clubId}/members")
@RequiredArgsConstructor
public class MemberAttendanceController {
    private final AttendanceService attendanceService;

    @Operation(summary = "내 출석 기록 조회", description = "로그인한 회원의 월별 출석 기록과 출석률을 조회합니다. year/month 미입력 시 현재 월 기준으로 조회됩니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "내 출석 기록 조회 성공")
    @ApiMemberErrorResponse
    @GetMapping("/me/records")
    public ResponseEntity<RsData<MyAttendanceRecordDto>> getMyRecords(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long clubId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int targetYear  = (year  != null) ? year  : LocalDate.now().getYear();
        int targetMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        MyAttendanceRecordDto response = attendanceService.getMyAttendanceRecord(memberId, clubId, targetYear, targetMonth);
        return ResponseEntity.ok(RsData.success(response));
    }
}