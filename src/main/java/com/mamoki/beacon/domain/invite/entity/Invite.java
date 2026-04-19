package com.mamoki.beacon.domain.invite.entity;
import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    // revokedAt: null이면 유효, 값 있으면 무효화된 시각
    public Invite(Member member, Club club, String inviteCode) {
        this.member = member;
        this.club = club;
        this.inviteCode = inviteCode;
        this.createdAt = LocalDateTime.now();
        this.revokedAt = null;
    }

    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }
}
