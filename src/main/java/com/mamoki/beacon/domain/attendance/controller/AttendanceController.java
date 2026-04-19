package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.AdminAttendanceRequestDto;
import com.mamoki.beacon.domain.attendance.dto.AttendanceCheckRequestDto;
import com.mamoki.beacon.domain.attendance.dto.AttendanceDto;
import com.mamoki.beacon.domain.attendance.dto.AttendanceStatusUpdateDto;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.global.rsdata.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs/{clubId}/sessions/{sessionId}/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping //출석체크
    public ResponseEntity<RsData<String>> checkAttendance(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId, @PathVariable Long sessionId, @RequestBody @Valid AttendanceCheckRequestDto request) {
        attendanceService.checkAttendance(memberId, clubId, sessionId, request.otpCode());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.success("출석이 완료되었습니다."));
    }

    // 세션별 출석 현황 조회 어드민 (10개씩)
    @GetMapping
    public ResponseEntity<RsData<Slice<AttendanceDto>>> getSessionAttendance(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId, @PathVariable Long sessionId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<AttendanceDto> result = attendanceService.getSessionAttendance(memberId, clubId, sessionId, pageable);
        return ResponseEntity.ok().body(RsData.success(result));
    }

    // 관리자 수동 출석 처리
    @PostMapping("/manual")
    public ResponseEntity<RsData<String>> manualCheckAttendance(@AuthenticationPrincipal Long adminId, @PathVariable Long clubId, @PathVariable Long sessionId, @RequestBody @Valid AdminAttendanceRequestDto request) {
        attendanceService.manualCheckAttendance(adminId, clubId, sessionId, request.memberId(), request.attendanceStatus(), request.adminNote());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RsData.success("수동 출석 처리되었습니다."));
    }

    // 출석 상태 수동 변경 어드민
    @PatchMapping("/{recordId}")
    public ResponseEntity<RsData<String>> updateStatus(@AuthenticationPrincipal Long adminId, @PathVariable Long clubId, @PathVariable Long sessionId, @PathVariable Long recordId, @RequestBody @Valid AttendanceStatusUpdateDto request) {
        attendanceService.updateStatus(adminId, clubId, recordId, request.attendanceStatus(), request.adminNote());
        return ResponseEntity.ok().body(RsData.success("출석 상태가 변경되었습니다."));
    }
}
