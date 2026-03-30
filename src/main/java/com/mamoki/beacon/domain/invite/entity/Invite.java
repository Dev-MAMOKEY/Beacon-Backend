package com.mamoki.beacon.domain.invite.entity;
import com.mamoki.beacon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
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
    @JoinColumn(name = "created_by")
    private Member member;
}
