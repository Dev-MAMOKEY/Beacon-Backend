package com.mamoki.beacon.domain.session.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SessionCreateRequestDto(
        @NotBlank String sessionName,
        @NotNull LocalDateTime expectStartAt,
        @NotNull LocalDateTime expectEndAt,

        // 반복 옵션 (없으면 단일 세션 생성)
        Boolean isRepeat,
        DayOfWeek dayOfWeek,
        LocalDate repeatEndDate
) {}
