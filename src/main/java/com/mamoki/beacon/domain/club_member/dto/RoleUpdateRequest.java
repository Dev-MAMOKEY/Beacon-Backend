package com.mamoki.beacon.domain.club_member.dto;

import com.mamoki.beacon.domain.club_member.entity.Role;

public record RoleUpdateRequest(
        Long clubId,
        Long requesterId, // 역할 변경 요청자
        Long targetMemberId, // 역할 변경 대상자
        Role newRole
){
}
