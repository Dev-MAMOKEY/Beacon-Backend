package com.mamoki.beacon.global.swagger;

/**
 * Swagger ApiResponse에서 사용하는 에러 JSON 예시 상수 모음
 * 어노테이션 내부에서는 동적 값을 사용할 수 없으므로 static final String 상수로 관리합니다.
 */
public final class SwaggerErrorExamples {

    private SwaggerErrorExamples() {}

    // ── 401 인증 오류 ──────────────────────────────────────────────────────────

    public static final String TOKEN_EXPIRED =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"TOKEN_EXPIRED\",\"message\":\"Access Token이 만료되었습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String TOKEN_INVALID =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"TOKEN_INVALID\",\"message\":\"토큰 형식이 잘못되었거나 서명이 유효하지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String INVALID_CREDENTIALS =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"INVALID_CREDENTIALS\",\"message\":\"학번 또는 비밀번호가 올바르지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String REFRESH_TOKEN_REVOKED =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"REFRESH_TOKEN_REVOKED\",\"message\":\"이미 무효화된 Refresh Token입니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String PASSWORD_CONFIRMATION_MISMATCH =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"PASSWORD_MISMATCH\",\"message\":\"현재 비밀번호가 일치하지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    // ── 403 권한 오류 ──────────────────────────────────────────────────────────

    public static final String FORBIDDEN =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"해당 리소스에 대한 권한이 없습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String NOT_CLUB_MEMBER =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"NOT_CLUB_MEMBER\",\"message\":\"해당 동아리의 멤버가 아닙니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String CLUB_ADMIN_REQUIRED =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"CLUB_ADMIN_REQUIRED\",\"message\":\"동아리 관리자만 수행할 수 있습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String CANNOT_UPDATE_OWN_ROLE =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"CANNOT_UPDATE_OWN_ROLE\",\"message\":\"자기 자신의 역할은 변경할 수 없습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    // ── 400 요청 오류 ──────────────────────────────────────────────────────────

    public static final String INVALID_INVITE_CODE =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"INVALID_INVITE_CODE\",\"message\":\"초대코드가 존재하지 않거나 유효하지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String CLUB_ALREADY_DELETED =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"CLUB_ALREADY_DELETED\",\"message\":\"삭제된 동아리입니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String SESSION_NOT_ACTIVE =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"SESSION_NOT_ACTIVE\",\"message\":\"현재 활성화된 세션이 없습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String SESSION_ALREADY_ENDED =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"SESSION_ALREADY_ENDED\",\"message\":\"종료된 세션은 수정할 수 없습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String NOT_DELETED_SESSION =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"NOT_DELETED_SESSION\",\"message\":\"활동중이라 삭제할 수 없는 세션입니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String OTP_GENERATION_FAILED =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"OTP_GENERATION_FAILED\",\"message\":\"출석 코드 생성에 실패했습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String INVALID_ATTENDANCE_CODE =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"INVALID_ATTENDANCE_CODE\",\"message\":\"출석 코드가 올바르지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String INVALID_MANUAL_STATUS =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"INVALID_MANUAL_STATUS\",\"message\":\"ABSENT는 수동 출석 처리에 사용할 수 없습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    // ── 404 리소스 없음 ────────────────────────────────────────────────────────

    public static final String MEMBER_NOT_FOUND =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"MEMBER_NOT_FOUND\",\"message\":\"해당 회원이 존재하지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String CLUB_NOT_FOUND =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"CLUB_NOT_FOUND\",\"message\":\"동아리가 존재하지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String SESSION_NOT_FOUND =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"SESSION_NOT_FOUND\",\"message\":\"해당 세션이 존재하지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String ATTENDANCE_NOT_FOUND =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"ATTENDANCE_NOT_FOUND\",\"message\":\"출석 기록이 존재하지 않습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    // ── 409 충돌 ───────────────────────────────────────────────────────────────

    public static final String DUPLICATE_STUDENT_ID =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"DUPLICATE_STUDENT_ID\",\"message\":\"이미 사용중인 학번입니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String ALREADY_CLUB_MEMBER =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"ALREADY_CLUB_MEMBER\",\"message\":\"이미 해당 동아리의 멤버입니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String ALREADY_CHECKED_IN =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"ALREADY_CHECKED_IN\",\"message\":\"이미 출석 처리된 세션입니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";

    public static final String SESSION_ALREADY_ACTIVE =
        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"SESSION_ALREADY_ACTIVE\",\"message\":\"이미 진행 중인 세션이 있습니다.\"},\"timestamp\":\"2025-04-25T10:00:00\"}";
}