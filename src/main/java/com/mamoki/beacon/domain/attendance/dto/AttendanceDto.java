package com.mamoki.beacon.domain.attendance.dto;

import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {
    private Long recordId;
    private Long memberId;
    private String memberName;
    private String stdId;
    private AttendanceStatus attendanceStatus;
    private LocalDateTime checkedAt;
    private Boolean isManual;
    private String adminNote;
}
