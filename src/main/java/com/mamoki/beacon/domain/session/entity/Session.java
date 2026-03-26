package com.mamoki.beacon.domain.session.entity;

import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Session extends GlobalEntity {
    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "late_threshold_minutes")
    private Integer lateThresholdMinutes;

    @Column(name = "scheduled_start_at") //예정 시작시간
    private LocalDateTime scheduledStartAt;

    @Column(name = "scheduled_end_at") //예정 종료시간
    private LocalDateTime scheduledEndAt;

    @Column(name = "start_at") //실제 시작시간
    private LocalDateTime startAt;

    @Column(name = "end_at") //실제 종료시간
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SessionStatus sessionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member member;
}
