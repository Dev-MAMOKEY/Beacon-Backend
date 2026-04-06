package com.mamoki.beacon.domain.member.dto.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberPaswordUpdateRequest(
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", // 숫자 영어 1개 이상 포함해야함
            message = "비밀번호는 영어와 숫자를 각각 1개 이상 포함해야 합니다."
    )
    String currentPassword,

     @NotBlank(message = "새 비밀번호 값은 필수입니다.")
     @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
     @Pattern(
             regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", // 숫자 영어 1개 이상 포함해야함
             message = "비밀번호는 영어와 숫자를 각각 1개 이상 포함해야 합니다."
     )
     String newPassword,

    @NotBlank(message = "비밀번호 확인 값은 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", // 숫자 영어 1개 이상 포함해야함
            message = "비밀번호는 영어와 숫자를 각각 1개 이상 포함해야 합니다."
    )
    String confirmNewPassword
) {
}
