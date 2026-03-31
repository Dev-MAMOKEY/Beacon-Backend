package com.mamoki.beacon.global.rsdata;

import com.mamoki.beacon.global.exception.ErrorCode;

import java.time.LocalDateTime;

public record RsData<T>( // 공통 API 응답 객체
        boolean success,
        T data,
        ErrorInfo error,
        LocalDateTime timestamp
) extends Throwable {
    public record ErrorInfo( // 에러코드에 들어갈 객체
            String code,
            String message
    ) {
    }

    // 성공 응답
    public static <T> RsData<T> success(T data) {
        return new RsData<>(true, data, null, LocalDateTime.now());
    }

    // 실패 응답
    public static <T> RsData<T> fail(ErrorCode errorCode) {
        return new RsData<>(false, null, new ErrorInfo(errorCode.getCode(), errorCode.getMessage()), LocalDateTime.now());
    }
}