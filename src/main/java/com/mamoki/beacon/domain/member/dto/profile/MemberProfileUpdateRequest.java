package com.mamoki.beacon.domain.member.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberProfileUpdateRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하이어야 합니다.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z]+$",
                message = "한글 또는 영문만 입력 가능합니다."
        )
        String name,
        boolean pushEnabled
) {
}
