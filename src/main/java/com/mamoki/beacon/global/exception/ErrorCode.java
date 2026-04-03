package com.mamoki.beacon.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 인증 관련 예외 처리 (401)
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "학번 또는 비밀번호가 올바르지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Access Token이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "토큰 형식이 잘못되었거나 서명이 유효하지 않습니다."),
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_REVOKED", "이미 무효화된 Refresh Token입니다."),

    // 권한 에러 (403)
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "해당 리소스에 대한 권한이 없습니다."),
    NOT_CLUB_MEMBER(HttpStatus.FORBIDDEN, "NOT_CLUB_MEMBER", "해당 동아리의 멤버가 아닙니다."),

    // 회원 및 동아리 관련 예외 (400, 409)
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "INVALID_INVITE_CODE", "초대코드가 존재하지 않거나 유효하지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "해당 회원이 존재하지 않습니다."),
    DUPLICATE_STUDENT_ID(HttpStatus.CONFLICT, "DUPLICATE_STUDENT_ID", "이미 사용중인 학번입니다."),
    ALREADY_CLUB_MEMBER(HttpStatus.CONFLICT, "ALREADY_CLUB_MEMBER", "이미 해당 동아리의 멤버입니다."),

    // 세션 관련 예외 (400, 404, 409)
    SESSION_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "SESSION_NOT_ACTIVE","현재 활성화된 세션이 없습니다."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION_NOT_FOUND", "해당 세션이 존재하지 않습니다."),
    SESSION_ALREADY_ACTIVE(HttpStatus.CONFLICT, "SESSION_ALREADY_ACTIVE", "이미 진행 중인 세션이 있습니다."),

    // 출석 관련 예외 (400, 409)
    INVALID_ATTENDANCE_CODE(HttpStatus.BAD_REQUEST, "INVALID_ATTENDANCE_CODE", "출석 코드가 올바르지 않습니다."),
    BEACON_NOT_DETECTED(HttpStatus.BAD_REQUEST, "BEACON_NOT_DETECTED", "비콘 신호가 감지되지 않았습니다."),
    ALREADY_CHECKED_IN(HttpStatus.CONFLICT, "ALREADY_CHECKED_IN","이미 출석 처리된 세션입니다."),

    // 서버 에러 (500)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR","서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}