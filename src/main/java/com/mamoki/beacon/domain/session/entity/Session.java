package com.mamoki.beacon.domain.session.entity;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session")
public class Session extends GlobalEntity {
    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "except_start_at")
    private LocalDateTime exceptStartAt;

    @Column(name = "except_end_at")
    private LocalDateTime exceptEndAt;

    @Column(name = "start_at") //실제 시작시간
    private LocalDateTime startAt;

    @Column(name = "end_at") //실제 종료시간
    private LocalDateTime endAt;

    @Column(name = "session_uuid")
    private String uuid;

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
