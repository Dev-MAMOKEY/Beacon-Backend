package com.mamoki.beacon.domain.club_member.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // JPA 복합키 동등성 비교할 때 사용함
public class ClubMemberId implements Serializable { // Member, Club의 기본키를 복합키로 받아오는 clubmember구현

    private Long memberId;
    private Long clubId;
}
