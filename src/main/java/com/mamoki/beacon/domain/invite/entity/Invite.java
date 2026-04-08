package com.mamoki.beacon.domain.invite.entity;
import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Invite {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_id")
    private int inviteId;

    @Column(name = "invite_code")
    private String inviteCode;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    //생성시간과 만료시간을 자동으로 등록하기 위한 생성자 생성
    public Invite(Member member, Club club, String inviteCode) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        this.member = member;
        this.club = club;
        this.inviteCode = inviteCode;
        this.createdAt = today.atStartOfDay();       // 오늘 00:00:00
        this.revokedAt = today.atTime(23, 59, 59);   // 오늘 23:59:59
    }

}
