package com.mamoki.beacon.domain.session.dto;

import com.mamoki.beacon.domain.session.entity.SessionCategory;
import com.mamoki.beacon.domain.session.entity.SessionRepeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SessionCreateRequestDto(
        @NotBlank String sessionName,
        @NotNull LocalDateTime expectStartAt,
        @NotNull LocalDateTime expectEndAt,

        //추가된 필드
        SessionCategory sessionCategory,
        String location,
        String description,
        SessionRepeatType sessionRepeatType,
        List<DayOfWeek> daysOfWeek,

        // 반복 옵션 (없으면 단일 세션 생성)
        Boolean isRepeat,
        LocalDate repeatEndDate
) {}
