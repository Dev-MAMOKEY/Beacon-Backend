package com.mamoki.beacon.domain.club_member.dto;

public record PartUpdateRequest(
        Long clubId,
        Long requesterId, // 파트 변경 요청자
        Long targetMemberId, // 파트 변경 대상자
        String newPart
){
}
