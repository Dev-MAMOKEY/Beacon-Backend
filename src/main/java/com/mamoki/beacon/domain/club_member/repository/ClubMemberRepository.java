package com.mamoki.beacon.domain.club_member.repository;

import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.ClubMemberId;
import com.mamoki.beacon.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, ClubMemberId> {

    // 특정 회원이 가입한 모든 ClubMember 조회 (Role 결정에 사용)
    List<ClubMember> findByMember(Member member);
    Optional<ClubMember> findByMemberIdAndClubId(Long memberId, Long clubId);

    // 역할 변경시 사용자 동아리와 번호를 조회 (없을 경우 예외처리)
    Optional<ClubMember> findByClubIdAndMemberId(Long clubId, Long memberId);
}