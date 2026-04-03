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
    private AttendanceStatus attendanceStatus;
    private Boolean isManual;
    private String adminNote;
}
