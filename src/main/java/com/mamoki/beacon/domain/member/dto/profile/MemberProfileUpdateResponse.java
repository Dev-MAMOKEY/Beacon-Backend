package com.mamoki.beacon.domain.member.dto.profile;

public record MemberProfileUpdateResponse(
        String name,
        boolean pushEnabled
) {
}
