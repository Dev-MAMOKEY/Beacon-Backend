package com.mamoki.beacon.domain.attendance.dto;

import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MyAttendanceRecordDto(int year, int month, List<AttendanceRecordItem> records, StatusSummary status, double attendanceRate) {
    public record AttendanceRecordItem(Long sessionId, String sessionName, LocalDate date, AttendanceStatus status, LocalDateTime checkedAt, String adminNote) {
    } // 1개의 세션당 1개의 출석기록이 있어서 그것들을 가져옴

    //매개변수 값이 null일 수가 없어 long으로 설정 Service에서 null이면 O을 주입
    public record StatusSummary(long present, long absent, long late, long etc) { //출석 상태별로 카운트 하기위한 record
    }
}
