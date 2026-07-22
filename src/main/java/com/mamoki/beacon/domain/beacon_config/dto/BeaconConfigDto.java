package com.mamoki.beacon.domain.beacon_config.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//GET 응답 / PUT 요청 공용 DTO (프론트 요구 필드명 그대로: uuid, lateThresholdMinutes, rssiStabilizationSeconds, rssiThreshold)
public record BeaconConfigDto(
        @NotBlank(message = "비콘 UUID는 필수입니다.")
        String uuid,

        @NotNull(message = "지각 기준 시간은 필수입니다.")
        @Min(value = 0, message = "지각 기준 시간은 0분 이상이어야 합니다.")
        Integer lateThresholdMinutes,

        @NotNull(message = "신호 안정화 시간은 필수입니다.")
        @Min(value = 1, message = "신호 안정화 시간은 1초 이상이어야 합니다.")
        Integer rssiStabilizationSeconds,

        @NotNull(message = "RSSI 임계값은 필수입니다.")
        @Min(value = -90, message = "RSSI 임계값은 -90 이상이어야 합니다.") //프론트 슬라이더 범위 -90 ~ -40
        @Max(value = -40, message = "RSSI 임계값은 -40 이하여야 합니다.")
        Integer rssiThreshold
) {
}
