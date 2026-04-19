package com.mamoki.beacon.domain.session.repository;

import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    boolean existsByClubIdAndSessionStatusAndDeletedAtIsNull(Long clubId, SessionStatus status);
    Slice<Session> findByClubIdAndSessionStatusAndDeletedAtIsNull(Long clubId, SessionStatus status, Pageable pageable);
    Slice<Session> findByClubIdAndDeletedAtIsNull(Long clubId, Pageable pageable);
    Optional<Session> findByIdAndDeletedAtIsNull(Long sessionId); // 상세 조회용
    Optional<Session> findByClubIdAndSessionStatusAndDeletedAtIsNull(Long clubId, SessionStatus status); // 활성 세션 단건 조회용
    long countByClubIdAndStartAtGreaterThanEqualAndDeletedAtIsNull(Long clubId, LocalDateTime joinedAt); // 출석률을 확인하기 위해 필요한 함수
}
