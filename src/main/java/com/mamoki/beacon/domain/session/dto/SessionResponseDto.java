package com.mamoki.beacon.domain.session.dto;

import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionCategory;
import com.mamoki.beacon.domain.session.entity.SessionStatus;

import java.time.LocalDateTime;

// 세션 조회 응답 DTO (엔티티를 직접 반환하면 LAZY 프록시 직렬화 오류가 발생해서 필요한 필드만 담아 반환)
public record SessionResponseDto(
        Long sessionId,
        String sessionName,
        SessionStatus status,
        SessionCategory category, // 세션 카테고리 (추가 필드)
        String location, // 세션 카드 장소? (추가된 필드)
        String description, // 세션 카드 설명? (추가된 필드)
        LocalDateTime expectStartAt, // 예정 시작 시각
        LocalDateTime expectEndAt,   // 예정 종료 시각
        LocalDateTime startAt,       // 실제 시작 시각 (시작 전이면 null)
        LocalDateTime endAt          // 실제 종료 시각 (종료 전이면 null)
) {
    // 엔티티 -> DTO 변환 함수
    public static SessionResponseDto from(Session session) {
        return new SessionResponseDto(
                session.getId(),
                session.getSessionName(),
                session.getSessionStatus(),
                session.getSessionCategory(),
                session.getLocation(),
                session.getDescription(),
                session.getExpectStartAt(),
                session.getExpectEndAt(),
                session.getStartAt(),
                session.getEndAt()
        );
    }
}
