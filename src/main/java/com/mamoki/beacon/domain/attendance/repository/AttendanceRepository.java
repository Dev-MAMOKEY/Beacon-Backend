package com.mamoki.beacon.domain.attendance.repository;

import com.mamoki.beacon.domain.attendance.entity.Attendance;
import com.mamoki.beacon.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySession(Session session);
}
