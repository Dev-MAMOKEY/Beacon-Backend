package com.mamoki.beacon.domain.club_member.repository;

import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.ClubMemberId;
import com.mamoki.beacon.domain.member.entity.Member;
import com.mamoki.beacon.domain.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, ClubMemberId> {
    // 특정 회원이 가입한 모든 ClubMember 조회 (Role 결정에 사용)
    List<ClubMember> findByMember(Member member);
    Optional<ClubMember> findByMemberIdAndClubId(Long memberId, Long clubId);
    List<ClubMember> findByClubId(Long clubId);
}