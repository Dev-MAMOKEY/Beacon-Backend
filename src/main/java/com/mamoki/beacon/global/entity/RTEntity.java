package com.mamoki.beacon.global.entity;

import com.mamoki.beacon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RTEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rt_id")
    private Long rtId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "revoked_at")
    private Date revokedAt;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
