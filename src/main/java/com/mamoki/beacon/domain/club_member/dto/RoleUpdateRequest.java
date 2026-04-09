package com.mamoki.beacon.domain.club_member.dto;

public record RoleUpdateRequest(
        int clubId,
        int memberId,
        String newRole
){
}
