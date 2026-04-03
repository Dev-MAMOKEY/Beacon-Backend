package com.mamoki.beacon.domain.member.dto.profile;

public record MemberProfileUpdateResponse(
        String newName,
        boolean changePushEnabled
) {
}
