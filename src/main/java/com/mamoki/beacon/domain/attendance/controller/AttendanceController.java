package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.AdminAttendanceRequestDto;
import com.mamoki.beacon.domain.attendance.dto.AttendanceCheckRequestDto;
import com.mamoki.beacon.domain.attendance.dto.AttendanceDto;
import com.mamoki.beacon.domain.attendance.dto.AttendanceStatusUpdateDto;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.global.rsdata.RsData;
import com.mamoki.beacon.global.swagger.ApiAdminErrorResponse;
import com.mamoki.beacon.global.swagger.ApiMemberErrorResponse;
import com.mamoki.beacon.global.swagger.SwaggerErrorExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Attendance", description = "출석 API")
@RestController
@RequestMapping("/api/v1/clubs/{clubId}/sessions/{sessionId}/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Operation(summary = "출석 체크", description = "OTP 코드를 입력하여 출석을 처리합니다. 세션 시작 후 비콘 설정의 지각 기준 시간(기본 10분) 이내 출석 시 PRESENT, 이후 LATE로 처리됩니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "201", description = "출석 처리 성공")
    @ApiResponse(responseCode = "400", description = "세션이 활성화 상태가 아니거나 OTP 코드가 올바르지 않은 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "SESSION_NOT_ACTIVE",      value = SwaggerErrorExamples.SESSION_NOT_ACTIVE),
            @ExampleObject(name = "INVALID_ATTENDANCE_CODE", value = SwaggerErrorExamples.INVALID_ATTENDANCE_CODE)
        }))
    @ApiResponse(responseCode = "404", description = "세션 또는 회원이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "SESSION_NOT_FOUND", value = SwaggerErrorExamples.SESSION_NOT_FOUND),
            @ExampleObject(name = "MEMBER_NOT_FOUND",  value = SwaggerErrorExamples.MEMBER_NOT_FOUND)
        }))
    @ApiResponse(responseCode = "409", description = "이미 출석 처리된 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.ALREADY_CHECKED_IN)))
    @ApiMemberErrorResponse
    @PostMapping
    public ResponseEntity<RsData<String>> checkAttendance(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId, @PathVariable Long sessionId, @RequestBody @Valid AttendanceCheckRequestDto request) {
        attendanceService.checkAttendance(memberId, clubId, sessionId, request.otpCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(RsData.success("출석이 완료되었습니다."));
    }

    @Operation(summary = "세션별 출석 현황 조회", description = "세션에 대한 전체 출석 현황을 페이지네이션으로 조회합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "출석 현황 조회 성공")
    @ApiResponse(responseCode = "404", description = "세션이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.SESSION_NOT_FOUND)))
    @ApiAdminErrorResponse
    @GetMapping
    public ResponseEntity<RsData<Slice<AttendanceDto>>> getSessionAttendance(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId, @PathVariable Long sessionId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<AttendanceDto> result = attendanceService.getSessionAttendance(memberId, clubId, sessionId, pageable);
        return ResponseEntity.ok().body(RsData.success(result));
    }

    @Operation(summary = "관리자 수동 출석 처리", description = "관리자가 특정 멤버를 수동으로 출석 처리합니다. ABSENT 상태는 수동 처리할 수 없습니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "201", description = "수동 출석 처리 성공")
    @ApiResponse(responseCode = "400", description = "ABSENT 상태로 수동 처리 시도 또는 세션이 활성화 상태가 아닌 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "INVALID_MANUAL_STATUS", value = SwaggerErrorExamples.INVALID_MANUAL_STATUS),
            @ExampleObject(name = "SESSION_NOT_ACTIVE",    value = SwaggerErrorExamples.SESSION_NOT_ACTIVE)
        }))
    @ApiResponse(responseCode = "404", description = "세션 또는 대상 멤버가 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "SESSION_NOT_FOUND", value = SwaggerErrorExamples.SESSION_NOT_FOUND),
            @ExampleObject(name = "MEMBER_NOT_FOUND",  value = SwaggerErrorExamples.MEMBER_NOT_FOUND)
        }))
    @ApiResponse(responseCode = "409", description = "대상 멤버가 이미 출석 처리된 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.ALREADY_CHECKED_IN)))
    @ApiAdminErrorResponse
    @PostMapping("/manual")
    public ResponseEntity<RsData<String>> manualCheckAttendance(@AuthenticationPrincipal Long adminId, @PathVariable Long clubId, @PathVariable Long sessionId, @RequestBody @Valid AdminAttendanceRequestDto request) {
        attendanceService.manualCheckAttendance(adminId, clubId, sessionId, request.memberId(), request.attendanceStatus(), request.adminNote());
        return ResponseEntity.status(HttpStatus.CREATED).body(RsData.success("수동 출석 처리되었습니다."));
    }

    @Operation(summary = "출석 상태 변경", description = "특정 출석 기록의 상태를 변경합니다. ADMIN만 가능합니다.",
        security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "출석 상태 변경 성공")
    @ApiResponse(responseCode = "404", description = "출석 기록이 존재하지 않는 경우",
        content = @Content(mediaType = "application/json", examples =
            @ExampleObject(value = SwaggerErrorExamples.ATTENDANCE_NOT_FOUND)))
    @ApiAdminErrorResponse
    @PatchMapping("/{recordId}")
    public ResponseEntity<RsData<String>> updateStatus(@AuthenticationPrincipal Long adminId, @PathVariable Long clubId, @PathVariable Long sessionId, @PathVariable Long recordId, @RequestBody @Valid AttendanceStatusUpdateDto request) {
        attendanceService.updateStatus(adminId, clubId, recordId, request.attendanceStatus(), request.adminNote());
        return ResponseEntity.ok().body(RsData.success("출석 상태가 변경되었습니다."));
    }
}