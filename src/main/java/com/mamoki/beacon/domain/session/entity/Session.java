package com.mamoki.beacon.domain.session.entity;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session")
public class Session extends GlobalEntity {
    @Column(name = "session_name")
    private String sessionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_category")
    private SessionCategory sessionCategory;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    @Column(name = "expect_start_at")
    private LocalDateTime expectStartAt;

    @Column(name = "expect_end_at")
    private LocalDateTime expectEndAt;

    @Column(name = "start_at") //실제 시작시간
    private LocalDateTime startAt;

    @Column(name = "end_at") //실제 종료시간
    private LocalDateTime endAt;

    @Column(name = "session_uuid")
    private String uuid;

    @Column(name = "attendance_otp")
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SessionStatus sessionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;
}
