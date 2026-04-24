package com.mamoki.beacon.domain.attendance.controller;

import com.mamoki.beacon.domain.attendance.dto.MyAttendanceRecordDto;
import com.mamoki.beacon.domain.attendance.service.AttendanceService;
import com.mamoki.beacon.global.rsdata.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/clubs/{clubId}/members")
@RequiredArgsConstructor
public class MemberAttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/me/records") //내 출석률 조회 api
    public ResponseEntity<RsData<MyAttendanceRecordDto>> getMyRecords(@AuthenticationPrincipal Long memberId, @PathVariable Long clubId, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {

        // year, month이 null인 경우 현재 년도와 월로 설정
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        int targetMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        MyAttendanceRecordDto response = attendanceService.getMyAttendanceRecord(memberId, clubId, targetYear, targetMonth);
        return ResponseEntity.ok(RsData.success(response));
    }
}
