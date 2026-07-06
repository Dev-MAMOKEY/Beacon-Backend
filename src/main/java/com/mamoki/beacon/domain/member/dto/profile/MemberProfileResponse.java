package com.mamoki.beacon.domain.member.dto.profile;

import java.util.List;

public record MemberProfileResponse(
        String name,
        List<Long> clubIds, // 클럽 ID (비어있을 경우 빈 배열 값으로 제공)
        String stdId,
        boolean pushEnabled
) {
}
