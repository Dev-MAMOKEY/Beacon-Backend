package com.mamoki.beacon.global.exception;

import com.mamoki.beacon.global.rsdata.RsData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 컨트롤러에 대해 전역적으로 예외 처리하기 위해 사용하는 어노테이션
public class GlobalExceptionHandler {

    // ErrorCode 순서 기준으로 예외 순서 정리함

    // 인증 관련 예외 처리 (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RsData<Void>> handleBadCredentials (BadCredentialsException exception) {
        return ResponseEntity
                .status(ErrorCode.INVALID_CREDENTIALS.getHttpStatus())
                .body(RsData.fail(ErrorCode.INVALID_CREDENTIALS));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<RsData<Void>> handle(ExpiredJwtException exception) {
        return ResponseEntity
                .status(ErrorCode.TOKEN_EXPIRED.getHttpStatus())
                .body(RsData.fail(ErrorCode.TOKEN_EXPIRED));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<RsData<Void>> handle(JwtException exception) {
        return ResponseEntity
                .status(ErrorCode.TOKEN_INVALID.getHttpStatus())
                .body(RsData.fail(ErrorCode.TOKEN_INVALID));
    }

    // 권한 관련 예외 처리 (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RsData<Void>> handle(AccessDeniedException exception) {
        return ResponseEntity
                .status(ErrorCode.FORBIDDEN.getHttpStatus())
                .body(RsData.fail(ErrorCode.FORBIDDEN));
    }

    // 존재하지 않는 경우 예외 처리 (404) -> 모든 엔티티 못 찾는 경우에 보통 사용함 (현재는 세션으로만 적용)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RsData<Void>> handle(EntityNotFoundException exception) {
        return ResponseEntity
                .status(ErrorCode.SESSION_NOT_FOUND.getHttpStatus())
                .body(RsData.fail(ErrorCode.SESSION_NOT_FOUND));
    }

    // 커스텀 예외 처리 (나머지 예외는 스프링에서 자동으로 적용시켜주는 것이 없어 커스텀으로 처리함)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RsData<Void>> handle(CustomException exception) {
        return ResponseEntity
                .status(exception.getErrorCode().getHttpStatus())
                .body(RsData.fail(exception.getErrorCode()));
    }

    // 서버 에러 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handle(Exception exception) {
        log.error("[500 Internal Server Error] {}: {}", exception.getClass().getName(), exception.getMessage(), exception);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(RsData.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
