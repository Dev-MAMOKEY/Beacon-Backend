package com.mamoki.beacon.domain.club_member.entity;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.global.entity.GlobalEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "club_member")
public class ClubMember extends GlobalEntity { // GlobalEntity를 상속해서 softDelete() 메서드 사용하기 위함

    @EmbeddedId
    private ClubMemberId id;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("clubId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    public ClubMember(Member member, Club club, Role role) {
        this.id = new ClubMemberId(member.getId(), club.getId());
        this.member = member;
        this.club = club;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    // 역할 변경을 위한 메서드
    public void updateRole(Role newRole) {
        this.role = newRole;
    }
}
