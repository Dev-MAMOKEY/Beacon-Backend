package com.mamoki.beacon.domain.session.repository;

import com.mamoki.beacon.domain.session.entity.Session;
import com.mamoki.beacon.domain.session.entity.SessionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    boolean existsByClubIdAndSessionStatusAndDeletedAtIsNull(Long clubId, SessionStatus status);
    Slice<Session> findByClubId(Long clubId, Pageable pageable);
    Slice<Session> findByClubIdAndSessionStatusAndDeletedAtIsNull(Long clubId, SessionStatus status, Pageable pageable); // status상태 필터
    Slice<Session> findByClubIdAndDeletedAtIsNull(Long clubId, Pageable pageable); // 걍 deleted된건 안보여주게
}
