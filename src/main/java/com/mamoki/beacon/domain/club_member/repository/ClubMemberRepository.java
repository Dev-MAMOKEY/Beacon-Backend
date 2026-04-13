package com.mamoki.beacon.domain.club_member.repository;

import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    Optional<ClubMember> findByMemberIdAndClubId(Long memberId, Long clubId);

    // 멤버 목록 조회 (soft_deleted 지정된 멤버는 제외)
    List<ClubMember> findByClubIdAndDeletedAtIsNull(Long clubId);
}