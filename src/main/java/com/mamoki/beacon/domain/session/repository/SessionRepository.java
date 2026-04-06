package com.mamoki.beacon.domain.session.repository;

import com.mamoki.beacon.domain.session.entity.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Slice<Session> findByClubId(Long clubId, Pageable pageable);
}
