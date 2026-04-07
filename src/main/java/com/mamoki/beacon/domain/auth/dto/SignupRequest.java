package com.mamoki.beacon.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record SignupRequest (
    @NotNull(message = "아이디(학번)는 필수입니다.")
    @Size(min = 8, message = "학번은 8자리 입니다.")
    int stdId,
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", // 숫자 영어 1개 이상 포함해야함
            message = "비밀번호는 영어와 숫자를 각각 1개 이상 포함해야 합니다."
    )
    String password,
    @NotNull(message = "학년은 필수입니다.")
    int grade,
    @NotBlank(message = "이름은 필수입니다.")
    String name
) {
}
