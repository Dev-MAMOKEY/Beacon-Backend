package com.mamoki.beacon.global.exception;

import com.mamoki.beacon.global.rsdata.RsData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@RestControllerAdvice // 컨트롤러에 대해 전역적으로 예외 처리하기 위해 사용하는 어노테이션
public class GlobalExceptionHandler {

    // ErrorCode 순서 기준으로 예외 순서 정리함

    // 요청 값 검증 실패 (400) -> @Valid 검증 실패 시 첫 번째 필드의 에러 메시지를 응답에 담아 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handle(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.INVALID_INPUT.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(RsData.fail(ErrorCode.INVALID_INPUT, message));
    }

    // 요청 본문 파싱 실패 (400) -> JSON 형식이 잘못됐거나 필드 타입이 맞지 않는 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RsData<Void>> handle(HttpMessageNotReadableException exception) {
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(RsData.fail(ErrorCode.INVALID_INPUT, "요청 본문(JSON) 형식이 올바르지 않습니다."));
    }

    // 요청 파라미터 타입 불일치 (400) -> 쿼리/경로 파라미터가 enum이나 숫자로 변환되지 않는 경우
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RsData<Void>> handle(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getHttpStatus())
                .body(RsData.fail(ErrorCode.INVALID_INPUT, "요청 파라미터 '" + exception.getName() + "' 값이 올바르지 않습니다."));
    }

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
    public ResponseEntity<RsData<Void>> handle(CustomException exception, HttpServletRequest request) {
        allowJsonErrorResponse(request);
        return ResponseEntity
                .status(exception.getErrorCode().getHttpStatus())
                .body(RsData.fail(exception.getErrorCode()));
    }

    // 서버 에러 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handle(Exception exception, HttpServletRequest request) {
        allowJsonErrorResponse(request);
        log.error("[500 Internal Server Error] {}: {}", exception.getClass().getName(), exception.getMessage(), exception);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(RsData.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    // 에러 응답(JSON)을 내보낼 수 있게 "이 요청이 생산 가능한 타입" 제한을 풀어준다.
    // SSE(/attendance/stream)처럼 produces = text/event-stream 으로 고정된 엔드포인트는
    // 이 제한 때문에 RsData(JSON)를 못 써서 HttpMediaTypeNotAcceptableException이 나고,
    // 원래 에러(예: 403 CLUB_ADMIN_REQUIRED)가 통째로 사라진 채 엉뚱한 401로 응답되던 문제가 있었다.
    private void allowJsonErrorResponse(HttpServletRequest request) {
        request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
    }
}
