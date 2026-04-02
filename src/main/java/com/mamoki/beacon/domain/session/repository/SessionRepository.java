package com.mamoki.beacon.domain.session.repository;

import com.mamoki.beacon.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
