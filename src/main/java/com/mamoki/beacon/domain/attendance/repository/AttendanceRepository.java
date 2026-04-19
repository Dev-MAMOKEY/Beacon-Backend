package com.mamoki.beacon.domain.attendance.repository;

import com.mamoki.beacon.domain.attendance.entity.Attendance;
import com.mamoki.beacon.domain.attendance.entity.AttendanceStatus;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.session.entity.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySession(Session session);
    Slice<Attendance> findBySession(Session session, Pageable pageable);

    boolean existsByMemberAndSession(Member member, Session session);
    Optional<Attendance> findByMemberAndSession(Member member, Session session);

    //출석률을 구하기 위한 쿼리문 (서비스에 안만들고 쿼리로 뺀 이유 : 책임분리원칙때문)
    @Query("""
           SELECT COUNT(a)
             FROM Attendance a
            WHERE a.member.id = :memberId
              AND a.session.club.id = :clubId
              AND a.session.deletedAt IS NULL
              AND a.session.startAt >= :joinedAt
              AND a.attendanceStatus IN :statuses
           """)
    //쿼리 결과 담는 변수
    long countAttendedByMemberAndClub(
            @Param("memberId") Long memberId,
            @Param("clubId") Long clubId,
            @Param("joinedAt") LocalDateTime joinedAt,
            @Param("statuses") List<AttendanceStatus> statuses
    );
}
