package com.mamoki.beacon.domain.attendance.repository;

import com.mamoki.beacon.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
