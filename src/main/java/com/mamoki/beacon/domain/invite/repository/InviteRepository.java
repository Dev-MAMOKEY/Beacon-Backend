package com.mamoki.beacon.domain.invite.repository;

import com.mamoki.beacon.domain.invite.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, Long> {
}
