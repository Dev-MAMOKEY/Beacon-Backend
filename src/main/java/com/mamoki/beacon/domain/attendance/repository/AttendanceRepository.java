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

    //월별 출석 기록 조회 하기위한 쿼리문
    @Query("""
           SELECT a FROM Attendance a
           JOIN FETCH a.session s
           WHERE a.member.id = :memberId
             AND s.club.id = :clubId
             AND s.deletedAt IS NULL
             AND a.deletedAt IS NULL
             AND YEAR(s.startAt) = :year
             AND MONTH(s.startAt) = :month
           ORDER BY s.startAt ASC
           """)
    List<Attendance> findMonthlyRecordsByMemberAndClub( //리스트형식으로 저장
            @Param("memberId") Long memberId,
            @Param("clubId") Long clubId,
            @Param("year") int year,
            @Param("month") int month
    );

    //세션별 상태별 인원수 가져오는 쿼리문
    @Query("""
       SELECT a.session.id, a.session.sessionName, a.session.startAt, a.attendanceStatus, COUNT(a)
       FROM Attendance a
       WHERE a.session.club.id = :clubId
         AND a.session.deletedAt IS NULL
         AND a.deletedAt IS NULL
         AND a.session.startAt BETWEEN :startAt AND :endAt
         AND a.session.sessionStatus = com.mamoki.beacon.domain.session.entity.SessionStatus.ENDED
       GROUP BY a.session.id, a.session.sessionName, a.session.startAt, a.attendanceStatus
       ORDER BY a.session.startAt ASC
       """)
    List<Object[]> countBySessionAndStatus(
            @Param("clubId") Long clubId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}
