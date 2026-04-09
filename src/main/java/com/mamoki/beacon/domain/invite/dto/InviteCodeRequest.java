package com.mamoki.beacon.domain.invite.dto;

import javax.management.relation.Role;

// 동아리 초대코드 발급을 받기 위해 요청하는 dto
public record InviteCodeRequest(
        int clubId
) {
}

