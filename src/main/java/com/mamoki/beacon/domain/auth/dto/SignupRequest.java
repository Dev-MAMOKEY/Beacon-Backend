package com.mamoki.beacon.domain.auth.dto;

public record SignupRequest (
    @NotBlank(message = "아이디(학번)는 필수입니다.")
    int studentId,
    @NotBlank(message = "비밀번호는 필수입니다.")
    String password,
    @NotBlank(message = "이름은 필수입니다.")
    String name
) {
}
