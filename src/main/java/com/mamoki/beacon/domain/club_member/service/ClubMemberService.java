package com.mamoki.beacon.domain.club_member.service;

import com.mamoki.beacon.domain.club_member.dto.RoleUpdateRequest;
import com.mamoki.beacon.domain.club_member.entity.ClubMember;
import com.mamoki.beacon.domain.club_member.entity.Role;
import com.mamoki.beacon.domain.club_member.repository.ClubMemberRepository;
import com.mamoki.beacon.domain.member.repository.MemberRepository;
import com.mamoki.beacon.global.exception.CustomException;
import com.mamoki.beacon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final MemberRepository memberRepository;

    // 회원 역할 변경 로직
    @Transactional
    public void updateMemberRole(RoleUpdateRequest request) {

        // 1. 요청자 회원정보 존재하는지 확인
        memberRepository.findById(request.requesterId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 요청자가 해당 동아리에 가입이 되어있는지 확인
        ClubMember requestMember = clubMemberRepository.findByMemberIdAndClubId(request.requesterId(), request.clubId())
                .orElseThrow(() -> new IllegalArgumentException("요청자가 해당 동아리에 가입되어 있지 않습니다."));

        // 3. 요청자가 ADMIN 권한인지 확인
        if (requestMember.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("동아리 관리자만 수행할 수 있습니다");
        }

        // 4. 변경 대상 회원이 존재하는지 확인
        memberRepository.findById(request.targetMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 5. 변경 대상 회원이 해당 동아리에 가입 되어 있는지
        ClubMember targetMember = clubMemberRepository.findByMemberIdAndClubId(request.targetMemberId(), request.clubId())
                .orElseThrow(() -> new IllegalArgumentException("변경 대상이 해당 동아리에 가입되어 있지 않습니다."));

        // 6. 요청자가 본인의 역할을 변경하려고 할 시
        if (request.requesterId().equals(request.targetMemberId())) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 7. 위 조건을 모두 만족할 경우 역할 변경
        targetMember.updateRole(request.newRole());
    }
}
