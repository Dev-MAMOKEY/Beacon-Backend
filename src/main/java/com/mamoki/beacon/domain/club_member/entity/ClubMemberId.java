package com.mamoki.beacon.domain.club_member.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClubMemberId implements Serializable { // Member, Club의 기본키를 복합키로 받아오는 clubmember구현

    private int memberId;
    private int clubId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClubMemberId)) return false;
        ClubMemberId that = (ClubMemberId) o;
        return memberId == that.memberId && clubId == that.clubId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, clubId);
    }
}
