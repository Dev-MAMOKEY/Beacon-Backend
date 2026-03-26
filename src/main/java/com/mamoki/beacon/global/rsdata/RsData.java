package com.mamoki.beacon.global.rsdata;

import java.time.LocalDateTime;

public record RsData<T>(
        boolean success,
        T data,
        ErrorInfo error,
        LocalDateTime timestamp
) {
    public record ErrorInfo(
            String code,
            String message
    ) {
    }

    // 성공 응답
    public static <T> RsData<T> success(T data) {
        return new RsData<>(true, data, null, LocalDateTime.now());
    }

    // 실패 응답
    public static <T> RsData<T> error(ErrorInfo error) {
        return new RsData<>(false, null, new ErrorInfo(error.code, error.message), LocalDateTime.now());
    }
}