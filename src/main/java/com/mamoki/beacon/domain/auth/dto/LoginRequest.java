package com.mamoki.beacon.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "아이디(학번)는 필수입니다.")
        int stdId,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
