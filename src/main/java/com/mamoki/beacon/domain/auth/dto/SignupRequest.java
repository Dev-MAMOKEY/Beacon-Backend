package com.mamoki.beacon.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest (
    @NotBlank(message = "아이디(학번)는 필수입니다.")
    int stdId,
    @NotBlank(message = "비밀번호는 필수입니다.")
    String password,
    @NotBlank(message = "학년은 필수입니다.")
    int grade,
    @NotBlank(message = "이름은 필수입니다.")
    String name
) {
}
