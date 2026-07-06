package com.mamoki.beacon.domain.club_member.dto;

import com.mamoki.beacon.domain.club_member.entity.Role;

public record ClubMemberResponse(
        Long memberId,
        String name,
        String stdId,
        Role role,
        double rate
) {
}
