package com.mamoki.beacon.domain.club_member.dto;

import com.mamoki.beacon.domain.club_member.entity.Role;

public record ClubMemberResponse(
        Long memberId,
        String name,
        int stdId,
        Role role
        // 출석률 추가해야함
) {
}
