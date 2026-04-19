package com.mamoki.beacon.domain.invite.repository;

import com.mamoki.beacon.domain.club.entity.Club;
import com.mamoki.beacon.domain.invite.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByClubAndRevokedAtIsNull(Club club);
    Optional<Invite> findByInviteCodeAndClub(String inviteCode, Club club);
}
