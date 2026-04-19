package com.mamoki.beacon.domain.attendance.entity;

import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.session.entity.Session;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendance", uniqueConstraints = { //출석 중복체크를 위해 A요청 딜레이가 걸리고 B요청이 들어오면 중복처리를 서버에서 해도 안됨
        @UniqueConstraint(name = "uk_attendance_member_session", columnNames = {"member_id", "session_id"})
})
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @Column(name = "attendance_status")
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    public void updateStatus(AttendanceStatus status, String adminNote) {
        this.attendanceStatus = status;
        this.adminNote = adminNote;
        this.updatedAt = LocalDateTime.now();
    }
}
