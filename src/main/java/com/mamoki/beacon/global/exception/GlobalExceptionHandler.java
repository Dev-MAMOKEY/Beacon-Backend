package com.mamoki.beacon.global.exception;

import ch.qos.logback.core.spi.ErrorCodes;
import com.mamoki.beacon.global.rsdata.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 컨트롤러에 대해 전역적으로 예외 처리하기 위해 사용하는 어노테이션
public class GlobalExceptionHandler {

    // ErrorCode 순서 기준으로 예외 순서 정리함

    // 인증 관련 예외 처리 (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RsData<Void>> handle(BadCredentialsException exception) {
        return new ResponseEntity<>(new RsData<>(false, null, e)
    }
}
