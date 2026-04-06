package com.mamoki.beacon.domain.member.dto.profile;

public record MemberProfileResponse(
        String name,
        int stdId,
        boolean pushEnabled
) {
}
