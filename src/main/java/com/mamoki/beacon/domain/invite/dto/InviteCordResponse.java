package com.mamoki.beacon.domain.invite.dto;

// 초대코드 요청 api 전달 후 서버에서 응답해주는 DTO
public record InviteCordResponse(
        String inviteCode
) {
}
