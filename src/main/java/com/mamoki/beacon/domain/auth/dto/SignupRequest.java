package com.mamoki.beacon.domain.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest (
    @NotBlank(message = "아이디(학번)는 필수입니다.")
    @Pattern(
            regexp = "^[A-Za-z0-9]{4,20}$", // 명세서 4-1: 학번은 영문/숫자 조합 4~20자
            message = "학번은 영문/숫자 조합 4~20자여야 합니다."
    )
    String stdId,
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", // 숫자 영어 1개 이상 포함해야함
            message = "비밀번호는 영어와 숫자를 각각 1개 이상 포함해야 합니다."
    )
    String password,
    @NotBlank(message = "이름은 필수입니다.")
    String name
) {
}
