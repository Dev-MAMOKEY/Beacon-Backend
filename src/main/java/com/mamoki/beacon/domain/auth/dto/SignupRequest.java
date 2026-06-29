package com.mamoki.beacon.domain.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest (
    @NotNull(message = "아이디(학번)는 필수입니다.")
    @Min(value = 10000000, message = "학번은 8자리여야 합니다.")
    @Max(value = 99999999, message = "학번은 8자리여야 합니다.")
    int stdId,
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
