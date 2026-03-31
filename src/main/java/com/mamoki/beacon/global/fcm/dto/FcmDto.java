package com.mamoki.beacon.global.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FcmDto {
    private String fcmToken; // 사용자의 fcm토큰을 서버로 보내기 위한 dto
}
