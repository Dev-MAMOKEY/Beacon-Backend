package com.mamoki.beacon.domain.member.dto.fcm;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenUpdateRequest(
        @NotBlank(message = "FCM 토큰은 필수입니다.")
        String fcmToken
) {
}
